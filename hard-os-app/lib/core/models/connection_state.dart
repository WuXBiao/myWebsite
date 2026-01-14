/// 硬件连接状态枚举
enum HardwareConnectionState {
  /// 未连接
  disconnected,
  
  /// 连接中
  connecting,
  
  /// 已连接
  connected,
  
  /// 断开连接中
  disconnecting,
  
  /// 连接失败
  failed,
  
  /// 连接超时
  timeout,
}

/// 硬件连接状态扩展
extension HardwareConnectionStateExtension on HardwareConnectionState {
  String get displayName {
    switch (this) {
      case HardwareConnectionState.disconnected:
        return '未连接';
      case HardwareConnectionState.connecting:
        return '连接中...';
      case HardwareConnectionState.connected:
        return '已连接';
      case HardwareConnectionState.disconnecting:
        return '断开连接中...';
      case HardwareConnectionState.failed:
        return '连接失败';
      case HardwareConnectionState.timeout:
        return '连接超时';
    }
  }

  bool get isConnected => this == HardwareConnectionState.connected;
  bool get isConnecting => this == HardwareConnectionState.connecting;
  bool get isDisconnected => this == HardwareConnectionState.disconnected;
}
