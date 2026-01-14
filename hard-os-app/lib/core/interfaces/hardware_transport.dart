import 'package:hard_os_app/core/models/connection_state.dart';
import 'package:hard_os_app/core/models/device_info.dart';
import 'package:hard_os_app/core/models/command_response.dart';

/// 硬件传输接口（HAL - Hardware Abstraction Layer）
/// 定义统一的硬件通信接口，支持多种传输协议
abstract class IHardwareTransport {
  /// 获取设备信息
  DeviceInfo get deviceInfo;
  
  /// 连接设备
  /// 
  /// [deviceId] 设备 ID
  /// 返回 true 表示连接成功
  Future<bool> connect(String deviceId);
  
  /// 断开连接
  Future<void> disconnect();
  
  /// 发送命令（字节数组）
  /// 
  /// [commandId] 命令唯一标识
  /// [bytes] 要发送的字节数据
  /// 返回 CommandResponse 响应对象
  Future<CommandResponse> sendCommand(String commandId, List<int> bytes);
  
  /// 发送命令（字符串）
  /// 
  /// [commandId] 命令唯一标识
  /// [command] 要发送的字符串数据
  /// 返回 CommandResponse 响应对象
  Future<CommandResponse> sendCommandString(String commandId, String command);
  
  /// 发送命令（十六进制字符串）
  /// 
  /// [commandId] 命令唯一标识
  /// [hexString] 十六进制字符串（如 "01 02 03"）
  /// 返回 CommandResponse 响应对象
  Future<CommandResponse> sendCommandHex(String commandId, String hexString);
  
  /// 数据接收流
  /// 业务层监听此流获取硬件返回的数据
  Stream<List<int>> get receiveStream;
  
  /// 连接状态流
  /// 业务层监听此流获取连接状态变化
  Stream<HardwareConnectionState> get stateStream;
  
  /// 获取当前连接状态
  HardwareConnectionState get currentState;
  
  /// 是否已连接
  bool get isConnected;
  
  /// 设置接收超时时间（毫秒）
  void setReceiveTimeout(Duration timeout);
  
  /// 清空接收缓冲区
  Future<void> clearReceiveBuffer();
  
  /// 获取传输统计信息
  TransportStats getStats();
}

/// 传输统计信息
class TransportStats {
  /// 发送的字节数
  int bytesSent = 0;
  
  /// 接收的字节数
  int bytesReceived = 0;
  
  /// 发送的命令数
  int commandsSent = 0;
  
  /// 接收的响应数
  int responsesReceived = 0;
  
  /// 连接次数
  int connectionAttempts = 0;
  
  /// 连接成功次数
  int successfulConnections = 0;
  
  /// 平均响应延迟（毫秒）
  double averageLatency = 0.0;
  
  /// 错误次数
  int errorCount = 0;

  @override
  String toString() => '''
TransportStats(
  bytesSent: $bytesSent,
  bytesReceived: $bytesReceived,
  commandsSent: $commandsSent,
  responsesReceived: $responsesReceived,
  connectionAttempts: $connectionAttempts,
  successfulConnections: $successfulConnections,
  averageLatency: ${averageLatency.toStringAsFixed(2)}ms,
  errorCount: $errorCount
)''';
}
