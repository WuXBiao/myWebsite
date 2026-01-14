/// 设备信息模型
class DeviceInfo {
  /// 设备 ID
  final String id;
  
  /// 设备名称
  final String name;
  
  /// 设备类型
  final DeviceType type;
  
  /// 信号强度（仅 BLE）
  final int? rssi;
  
  /// 设备地址
  final String? address;
  
  /// 是否已连接
  bool isConnected;
  
  /// 最后连接时间
  DateTime? lastConnectedTime;
  
  /// 连接次数
  int connectionCount;

  DeviceInfo({
    required this.id,
    required this.name,
    required this.type,
    this.rssi,
    this.address,
    this.isConnected = false,
    this.lastConnectedTime,
    this.connectionCount = 0,
  });

  /// 复制设备信息
  DeviceInfo copyWith({
    String? id,
    String? name,
    DeviceType? type,
    int? rssi,
    String? address,
    bool? isConnected,
    DateTime? lastConnectedTime,
    int? connectionCount,
  }) {
    return DeviceInfo(
      id: id ?? this.id,
      name: name ?? this.name,
      type: type ?? this.type,
      rssi: rssi ?? this.rssi,
      address: address ?? this.address,
      isConnected: isConnected ?? this.isConnected,
      lastConnectedTime: lastConnectedTime ?? this.lastConnectedTime,
      connectionCount: connectionCount ?? this.connectionCount,
    );
  }

  @override
  String toString() => 'DeviceInfo(id: $id, name: $name, type: $type, isConnected: $isConnected)';
}

/// 设备类型枚举
enum DeviceType {
  /// 低功耗蓝牙
  ble,
  
  /// 经典蓝牙
  classicBluetooth,
  
  /// USB 串口
  usb,
  
  /// Wi-Fi（预留）
  wifi,
}

/// 设备类型扩展
extension DeviceTypeExtension on DeviceType {
  String get displayName {
    switch (this) {
      case DeviceType.ble:
        return 'BLE';
      case DeviceType.classicBluetooth:
        return '经典蓝牙';
      case DeviceType.usb:
        return 'USB';
      case DeviceType.wifi:
        return 'Wi-Fi';
    }
  }

  String get icon {
    switch (this) {
      case DeviceType.ble:
        return '📡';
      case DeviceType.classicBluetooth:
        return '🔵';
      case DeviceType.usb:
        return '🔌';
      case DeviceType.wifi:
        return '📶';
    }
  }
}
