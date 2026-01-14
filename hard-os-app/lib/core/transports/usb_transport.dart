import 'dart:async';
import 'dart:typed_data';
import 'package:usb_serial/usb_serial.dart';
import 'package:hard_os_app/core/interfaces/hardware_transport.dart';
import 'package:hard_os_app/core/models/connection_state.dart';
import 'package:hard_os_app/core/models/device_info.dart';
import 'package:hard_os_app/core/models/command_response.dart';
import 'package:hard_os_app/core/protocol/protocol_parser.dart';
import 'package:logger/logger.dart';

/// USB 串口传输实现
class UsbTransport implements IHardwareTransport {
  final Logger _logger = Logger();
  
  late UsbPort _port;
  late UsbDevice _device;
  
  final StreamController<HardwareConnectionState> _stateController = StreamController.broadcast();
  final StreamController<List<int>> _receiveController = StreamController.broadcast();
  
  HardwareConnectionState _currentState = HardwareConnectionState.disconnected;
  
  final ProtocolParser _protocolParser;
  final TransportStats _stats = TransportStats();
  
  Duration _receiveTimeout = const Duration(seconds: 5);
  late StreamSubscription _dataSubscription;
  
  // 命令响应等待队列
  final Map<String, Completer<CommandResponse>> _pendingResponses = {};
  
  // USB 配置
  final int baudRate;

  UsbTransport({
    ProtocolParser? protocolParser,
    this.baudRate = 9600,
  }) : _protocolParser = protocolParser ?? DelimiterProtocolParser();

  @override
  DeviceInfo get deviceInfo => DeviceInfo(
    id: _device.deviceId.toString(),
    name: _device.productName ?? '未知 USB 设备',
    type: DeviceType.usb,
    address: _device.deviceId.toString(),
    isConnected: isConnected,
  );

  @override
  Future<bool> connect(String deviceId) async {
    try {
      _updateState(HardwareConnectionState.connecting);
      _stats.connectionAttempts++;
      
      // 获取 USB 设备列表
      List<UsbDevice> devices = await UsbSerial.listDevices();
      
      _device = devices.firstWhere(
        (device) => device.deviceId.toString() == deviceId,
        orElse: () => throw Exception('USB 设备未找到'),
      );
      
      _logger.i('开始连接 USB 设备: ${_device.productName}');
      
      // 打开端口
      bool openResult = await _port.open();
      if (!openResult) {
        throw Exception('无法打开 USB 端口');
      }
      
      // 配置串口参数
      await _port.setDTR(true);
      await _port.setRTS(true);
      
      _logger.i('USB 串口已配置: 波特率=$baudRate');
      
      // 监听接收数据
      _dataSubscription = _port.inputStream!.listen(
        _onDataReceived,
        onError: (error) {
          _logger.e('USB 数据接收错误: $error');
          _updateState(HardwareConnectionState.failed);
        },
        onDone: () {
          _logger.i('USB 连接已关闭');
          _updateState(HardwareConnectionState.disconnected);
        },
      );
      
      _updateState(HardwareConnectionState.connected);
      _stats.successfulConnections++;
      return true;
    } catch (e) {
      _logger.e('USB 连接失败: $e');
      _updateState(HardwareConnectionState.failed);
      _stats.errorCount++;
      return false;
    }
  }

  @override
  Future<void> disconnect() async {
    try {
      _updateState(HardwareConnectionState.disconnecting);
      
      await _dataSubscription.cancel();
      await _port.close();
      
      _updateState(HardwareConnectionState.disconnected);
      _logger.i('USB 设备已断开连接');
    } catch (e) {
      _logger.e('USB 断开连接失败: $e');
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
      
      // 发送数据
      await _port.write(Uint8List.fromList(bytes));
      
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
      _logger.i('USB 连接状态: ${state.displayName}');
    }
  }

  void _onDataReceived(Uint8List data) {
    _stats.bytesReceived += data.length;
    _logger.d('接收数据: ${_bytesToHex(data.toList())}');
    
    // 使用协议解析器解析数据
    List<List<int>> packets = _protocolParser.parse(data.toList());
    
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
