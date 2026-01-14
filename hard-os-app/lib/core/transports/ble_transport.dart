import 'dart:async';
import 'package:flutter_blue_plus/flutter_blue_plus.dart';
import 'package:hard_os_app/core/interfaces/hardware_transport.dart';
import 'package:hard_os_app/core/models/connection_state.dart';
import 'package:hard_os_app/core/models/device_info.dart';
import 'package:hard_os_app/core/models/command_response.dart';
import 'package:hard_os_app/core/protocol/protocol_parser.dart';
import 'package:logger/logger.dart';

/// BLE 传输实现
class BleTransport implements IHardwareTransport {
  final Logger _logger = Logger();
  
  late BluetoothDevice _device;
  late BluetoothCharacteristic _writeCharacteristic;
  late BluetoothCharacteristic _notifyCharacteristic;
  
  final StreamController<HardwareConnectionState> _stateController = StreamController.broadcast();
  final StreamController<List<int>> _receiveController = StreamController.broadcast();
  
  HardwareConnectionState _currentState = HardwareConnectionState.disconnected;
  
  final ProtocolParser _protocolParser;
  final TransportStats _stats = TransportStats();
  
  Duration _receiveTimeout = const Duration(seconds: 5);
  late StreamSubscription _notifySubscription;
  
  // 命令响应等待队列
  final Map<String, Completer<CommandResponse>> _pendingResponses = {};

  BleTransport({
    ProtocolParser? protocolParser,
  }) : _protocolParser = protocolParser ?? DelimiterProtocolParser();

  @override
  DeviceInfo get deviceInfo => DeviceInfo(
    id: _device.remoteId.toString(),
    name: _device.platformName,
    type: DeviceType.ble,
    address: _device.remoteId.toString(),
    isConnected: isConnected,
  );

  @override
  Future<bool> connect(String deviceId) async {
    try {
      _updateState(HardwareConnectionState.connecting);
      _stats.connectionAttempts++;
      
      // 获取设备
      _device = BluetoothDevice.fromId(deviceId);
      
      // 连接设备
      await _device.connect(timeout: const Duration(seconds: 10));
      _logger.i('BLE 设备已连接: $deviceId');
      
      // 发现服务
      List<BluetoothService> services = await _device.discoverServices();
      _logger.i('发现 ${services.length} 个服务');
      
      // 查找读写特征值
      bool foundCharacteristics = false;
      for (BluetoothService service in services) {
        for (BluetoothCharacteristic characteristic in service.characteristics) {
          if (characteristic.properties.write) {
            _writeCharacteristic = characteristic;
            _logger.i('找到写特征值: ${characteristic.uuid}');
          }
          if (characteristic.properties.notify) {
            _notifyCharacteristic = characteristic;
            _logger.i('找到通知特征值: ${characteristic.uuid}');
            foundCharacteristics = true;
          }
        }
      }
      
      if (!foundCharacteristics) {
        throw Exception('未找到必要的特征值');
      }
      
      // 申请 MTU（最大传输单元）
      await _device.requestMtu(512);
      _logger.i('已申请 MTU: 512');
      
      // 启用通知
      await _notifyCharacteristic.setNotifyValue(true);
      
      // 监听通知
      _notifySubscription = _notifyCharacteristic.onValueReceived.listen(
        _onDataReceived,
        onError: (error) {
          _logger.e('通知监听错误: $error');
          _updateState(HardwareConnectionState.failed);
        },
      );
      
      _updateState(HardwareConnectionState.connected);
      _stats.successfulConnections++;
      return true;
    } catch (e) {
      _logger.e('BLE 连接失败: $e');
      _updateState(HardwareConnectionState.failed);
      _stats.errorCount++;
      return false;
    }
  }

  @override
  Future<void> disconnect() async {
    try {
      _updateState(HardwareConnectionState.disconnecting);
      
      await _notifySubscription.cancel();
      await _notifyCharacteristic.setNotifyValue(false);
      await _device.disconnect();
      
      _updateState(HardwareConnectionState.disconnected);
      _logger.i('BLE 设备已断开连接');
    } catch (e) {
      _logger.e('BLE 断开连接失败: $e');
      _updateState(HardwareConnectionState.disconnected);
    }
  }

  @override
  Future<CommandResponse> sendCommand(String commandId, List<int> bytes) async {
    if (!isConnected) {
      throw Exception('设备未连接');
    }
    
    try {
      final completer = Completer<CommandResponse>();
      _pendingResponses[commandId] = completer;
      
      final startTime = DateTime.now();
      
      // 发送数据（BLE 有 MTU 限制，需要分包）
      await _sendBytesWithMtuLimit(bytes);
      
      _stats.bytesSent += bytes.length;
      _stats.commandsSent++;
      
      _logger.i('发送命令 $commandId: ${_bytesToHex(bytes)}');
      
      // 等待响应
      final response = await completer.future.timeout(
        _receiveTimeout,
        onTimeout: () {
          _pendingResponses.remove(commandId);
          throw TimeoutException('命令 $commandId 响应超时');
        },
      );
      
      response.latency = DateTime.now().difference(startTime).inMilliseconds;
      return response;
    } catch (e) {
      _logger.e('发送命令失败: $e');
      _stats.errorCount++;
      rethrow;
    }
  }

  @override
  Future<CommandResponse> sendCommandString(String commandId, String command) async {
    return sendCommand(commandId, command.codeUnits);
  }

  @override
  Future<CommandResponse> sendCommandHex(String commandId, String hexString) async {
    final bytes = _hexToBytes(hexString);
    return sendCommand(commandId, bytes);
  }

  @override
  Stream<List<int>> get receiveStream => _receiveController.stream;

  @override
  Stream<HardwareConnectionState> get stateStream => _stateController.stream;

  @override
  HardwareConnectionState get currentState => _currentState;

  @override
  bool get isConnected => _currentState == HardwareConnectionState.connected;

  @override
  void setReceiveTimeout(Duration timeout) {
    _receiveTimeout = timeout;
  }

  @override
  Future<void> clearReceiveBuffer() async {
    _protocolParser.reset();
  }

  @override
  TransportStats getStats() => _stats;

  // ==================== 私有方法 ====================

  void _updateState(HardwareConnectionState state) {
    if (_currentState != state) {
      _currentState = state;
      _stateController.add(state);
      _logger.i('BLE 连接状态: ${state.displayName}');
    }
  }

  void _onDataReceived(List<int> data) {
    _stats.bytesReceived += data.length;
    _logger.d('接收数据: ${_bytesToHex(data)}');
    
    // 使用协议解析器解析数据
    List<List<int>> packets = _protocolParser.parse(data);
    
    for (List<int> packet in packets) {
      _stats.responsesReceived++;
      _receiveController.add(packet);
      
      // 如果有待处理的命令，返回响应
      if (_pendingResponses.isNotEmpty) {
        final commandId = _pendingResponses.keys.first;
        final completer = _pendingResponses.remove(commandId);
        
        if (completer != null && !completer.isCompleted) {
          completer.complete(
            CommandResponse(
              commandId: commandId,
              data: packet,
              timestamp: DateTime.now(),
              success: true,
            ),
          );
        }
      }
    }
  }

  Future<void> _sendBytesWithMtuLimit(List<int> bytes) async {
    const int mtuLimit = 512;
    
    if (bytes.length <= mtuLimit) {
      await _writeCharacteristic.write(bytes, withoutResponse: false);
    } else {
      // 分包发送
      for (int i = 0; i < bytes.length; i += mtuLimit) {
        int end = (i + mtuLimit < bytes.length) ? i + mtuLimit : bytes.length;
        List<int> chunk = bytes.sublist(i, end);
        await _writeCharacteristic.write(chunk, withoutResponse: false);
        await Future.delayed(const Duration(milliseconds: 10));
      }
    }
  }

  String _bytesToHex(List<int> bytes) {
    return bytes.map((b) => b.toRadixString(16).padLeft(2, '0')).join(' ').toUpperCase();
  }

  List<int> _hexToBytes(String hexString) {
    final cleanHex = hexString.replaceAll(' ', '').replaceAll(':', '');
    final bytes = <int>[];
    
    for (int i = 0; i < cleanHex.length; i += 2) {
      bytes.add(int.parse(cleanHex.substring(i, i + 2), radix: 16));
    }
    
    return bytes;
  }
}
