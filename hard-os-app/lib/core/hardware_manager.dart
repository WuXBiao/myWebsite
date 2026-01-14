import 'dart:async';
import 'package:hard_os_app/core/interfaces/hardware_transport.dart';
import 'package:hard_os_app/core/models/connection_state.dart';
import 'package:hard_os_app/core/models/device_info.dart';
import 'package:hard_os_app/core/models/command_response.dart';
import 'package:hard_os_app/core/transports/ble_transport.dart';
import 'package:hard_os_app/core/transports/usb_transport.dart';
import 'package:logger/logger.dart';

/// 硬件管理器
/// 负责管理多种硬件传输协议，提供统一的接口
class HardwareManager {
  final Logger _logger = Logger();
  
  IHardwareTransport? _currentTransport;
  
  final StreamController<HardwareConnectionState> _stateController = StreamController.broadcast();
  final StreamController<List<int>> _dataController = StreamController.broadcast();
  final StreamController<CommandResponse> _responseController = StreamController.broadcast();
  
  // 自动重连配置
  bool _autoReconnect = true;
  int _maxReconnectAttempts = 3;
  Duration _reconnectDelay = const Duration(seconds: 2);
  int _currentReconnectAttempt = 0;
  Timer? _reconnectTimer;
  
  // 心跳包配置
  bool _enableHeartbeat = false;
  Duration _heartbeatInterval = const Duration(seconds: 30);
  Timer? _heartbeatTimer;
  String _heartbeatCommand = '0x00'; // 默认心跳命令

  HardwareManager();

  /// 创建 BLE 传输
  IHardwareTransport createBleTransport() {
    return BleTransport();
  }

  /// 创建经典蓝牙传输（已弃用，使用 BLE 替代）
  /// 注：flutter_bluetooth_serial 与新版 AGP 不兼容，建议使用 BLE
  IHardwareTransport createClassicBluetoothTransport() {
    throw UnsupportedError(
      '经典蓝牙已弃用。请使用 createBleTransport() 替代。'
      'flutter_bluetooth_serial 与新版 Android Gradle Plugin 不兼容。',
    );
  }

  /// 创建 USB 传输
  IHardwareTransport createUsbTransport({
    int baudRate = 9600,
  }) {
    return UsbTransport(
      baudRate: baudRate,
    );
  }

  /// 连接设备
  Future<bool> connect(IHardwareTransport transport, String deviceId) async {
    try {
      _currentTransport = transport;
      _currentReconnectAttempt = 0;
      
      // 监听状态变化
      _subscribeToTransport();
      
      // 连接设备
      bool success = await transport.connect(deviceId);
      
      if (success && _enableHeartbeat) {
        _startHeartbeat();
      }
      
      return success;
    } catch (e) {
      _logger.e('连接失败: $e');
      return false;
    }
  }

  /// 断开连接
  Future<void> disconnect() async {
    try {
      _stopHeartbeat();
      _stopReconnect();
      
      if (_currentTransport != null) {
        await _currentTransport!.disconnect();
      }
    } catch (e) {
      _logger.e('断开连接失败: $e');
    }
  }

  /// 发送命令
  Future<CommandResponse> sendCommand(String commandId, List<int> bytes) async {
    if (_currentTransport == null || !_currentTransport!.isConnected) {
      throw Exception('设备未连接');
    }
    
    try {
      final response = await _currentTransport!.sendCommand(commandId, bytes);
      _responseController.add(response);
      return response;
    } catch (e) {
      _logger.e('发送命令失败: $e');
      rethrow;
    }
  }

  /// 发送字符串命令
  Future<CommandResponse> sendCommandString(String commandId, String command) async {
    if (_currentTransport == null || !_currentTransport!.isConnected) {
      throw Exception('设备未连接');
    }
    
    return _currentTransport!.sendCommandString(commandId, command);
  }

  /// 发送十六进制命令
  Future<CommandResponse> sendCommandHex(String commandId, String hexString) async {
    if (_currentTransport == null || !_currentTransport!.isConnected) {
      throw Exception('设备未连接');
    }
    
    return _currentTransport!.sendCommandHex(commandId, hexString);
  }

  /// 获取连接状态流
  Stream<HardwareConnectionState> get stateStream => _stateController.stream;

  /// 获取数据流
  Stream<List<int>> get dataStream => _dataController.stream;

  /// 获取响应流
  Stream<CommandResponse> get responseStream => _responseController.stream;

  /// 获取当前连接状态
  HardwareConnectionState get currentState => _currentTransport?.currentState ?? HardwareConnectionState.disconnected;

  /// 是否已连接
  bool get isConnected => _currentTransport?.isConnected ?? false;

  /// 获取设备信息
  DeviceInfo? get deviceInfo => _currentTransport?.deviceInfo;

  /// 获取统计信息
  TransportStats? getStats() => _currentTransport?.getStats();

  /// 配置自动重连
  void configureAutoReconnect({
    bool enabled = true,
    int maxAttempts = 3,
    Duration delay = const Duration(seconds: 2),
  }) {
    _autoReconnect = enabled;
    _maxReconnectAttempts = maxAttempts;
    _reconnectDelay = delay;
  }

  /// 配置心跳包
  void configureHeartbeat({
    bool enabled = true,
    Duration interval = const Duration(seconds: 30),
    String command = '0x00',
  }) {
    _enableHeartbeat = enabled;
    _heartbeatInterval = interval;
    _heartbeatCommand = command;
  }

  // ==================== 私有方法 ====================

  void _subscribeToTransport() {
    if (_currentTransport == null) return;
    
    // 监听状态变化
    _currentTransport!.stateStream.listen((state) {
      _stateController.add(state);
      
      if (state == HardwareConnectionState.disconnected && _autoReconnect) {
        _startReconnect();
      } else if (state == HardwareConnectionState.connected) {
        _stopReconnect();
      }
    });
    
    // 监听数据接收
    _currentTransport!.receiveStream.listen((data) {
      _dataController.add(data);
    });
  }

  void _startReconnect() {
    if (_currentReconnectAttempt >= _maxReconnectAttempts) {
      _logger.w('已达到最大重连次数');
      return;
    }
    
    _currentReconnectAttempt++;
    _logger.i('准备第 $_currentReconnectAttempt 次重连，延迟 ${_reconnectDelay.inSeconds} 秒');
    
    _reconnectTimer = Timer(_reconnectDelay, () async {
      if (_currentTransport != null && _currentTransport!.deviceInfo != null) {
        _logger.i('尝试重连设备: ${_currentTransport!.deviceInfo!.name}');
        await connect(_currentTransport!, _currentTransport!.deviceInfo!.id);
      }
    });
  }

  void _stopReconnect() {
    _reconnectTimer?.cancel();
    _currentReconnectAttempt = 0;
  }

  void _startHeartbeat() {
    _logger.i('启动心跳包，间隔 ${_heartbeatInterval.inSeconds} 秒');
    
    _heartbeatTimer = Timer.periodic(_heartbeatInterval, (timer) async {
      if (isConnected) {
        try {
          await sendCommandHex('heartbeat', _heartbeatCommand);
          _logger.d('心跳包已发送');
        } catch (e) {
          _logger.e('心跳包发送失败: $e');
        }
      }
    });
  }

  void _stopHeartbeat() {
    _heartbeatTimer?.cancel();
    _logger.i('心跳包已停止');
  }

  /// 清理资源
  void dispose() {
    _stopHeartbeat();
    _stopReconnect();
    _stateController.close();
    _dataController.close();
    _responseController.close();
  }
}
