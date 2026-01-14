# 硬件通信应用（Hard OS App）

一个基于 Flutter 的专业硬件通信框架，支持 USB、BLE、经典蓝牙三模连接，采用硬件抽象层（HAL）架构设计。

## 🎯 核心特性

### 1. 多协议支持
- **BLE（低功耗蓝牙）** - 省电，适合偶发指令
- **经典蓝牙** - 稳定，适合频繁交互
- **USB 串口** - 最稳定，适合工业场景

### 2. 硬件抽象层（HAL）
统一的 `IHardwareTransport` 接口，业务层无需关心具体的传输协议实现。

```dart
// 业务层代码保持不变，只需切换传输实现
final transport = manager.createBleTransport();
await manager.connect(transport, deviceId);
```

### 3. 数据粘包处理
内置多种协议解析器：
- **定界符协议** - 使用 STX/ETX 标记数据包边界
- **定长协议** - 固定长度的数据包
- **自定义协议** - 支持用户自定义解析逻辑

### 4. 自动重连机制
- 监听连接断开事件
- 自动尝试重连（可配置次数和延迟）
- 提升用户体验

### 5. 心跳包机制
- 定期发送心跳包
- 检测设备是否离线
- 及时发现连接问题

### 6. 完整的日志系统
- 详细的操作日志
- 十六进制数据展示
- 传输统计信息

## 📁 项目结构

```
lib/
├── core/
│   ├── interfaces/
│   │   └── hardware_transport.dart      # HAL 接口定义
│   ├── models/
│   │   ├── connection_state.dart        # 连接状态
│   │   ├── device_info.dart             # 设备信息
│   │   └── command_response.dart        # 命令响应
│   ├── protocol/
│   │   └── protocol_parser.dart         # 协议解析器
│   ├── transports/
│   │   ├── ble_transport.dart           # BLE 实现
│   │   ├── classic_bluetooth_transport.dart  # 经典蓝牙实现
│   │   └── usb_transport.dart           # USB 实现
│   └── hardware_manager.dart            # 硬件管理器
├── pages/
│   └── home_page.dart                   # 主页面
├── widgets/
│   ├── connection_status.dart           # 连接状态显示
│   ├── device_scanner.dart              # 设备扫描
│   ├── command_sender.dart              # 命令发送
│   └── data_monitor.dart                # 数据监控
└── main.dart                            # 应用入口
```

## 🚀 快速开始

### 1. 安装依赖
```bash
flutter pub get
```

### 2. 配置权限

#### Android (AndroidManifest.xml)
```xml
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.USB_PERMISSION" />
```

### 3. 运行应用
```bash
flutter run
```

## 💻 使用示例

### 基础连接
```dart
final manager = HardwareManager();

// 创建 BLE 传输
final transport = manager.createBleTransport();

// 连接设备
bool success = await manager.connect(transport, deviceId);

if (success) {
  // 发送命令
  final response = await manager.sendCommandHex('cmd1', '01 02 03');
  print('响应: ${response.hexString}');
}
```

### 配置自动重连
```dart
manager.configureAutoReconnect(
  enabled: true,
  maxAttempts: 3,
  delay: const Duration(seconds: 2),
);
```

### 配置心跳包
```dart
manager.configureHeartbeat(
  enabled: true,
  interval: const Duration(seconds: 30),
  command: '0x00',
);
```

### 监听状态变化
```dart
manager.stateStream.listen((state) {
  print('连接状态: ${state.displayName}');
});
```

### 监听数据接收
```dart
manager.dataStream.listen((data) {
  print('接收数据: ${data.map((b) => b.toRadixString(16)).join(' ')}');
});
```

### 获取统计信息
```dart
final stats = manager.getStats();
print('发送字节数: ${stats?.bytesSent}');
print('接收字节数: ${stats?.bytesReceived}');
print('平均延迟: ${stats?.averageLatency}ms');
```

## 🔧 高级用法

### 自定义协议解析器
```dart
final parser = CustomProtocolParser(
  parseFunction: (buffer) {
    // 自定义解析逻辑
    if (buffer.length >= 10) {
      return buffer.sublist(0, 10);
    }
    return [];
  },
);

final transport = BleTransport(protocolParser: parser);
```

### 自定义 USB 配置
```dart
final transport = manager.createUsbTransport(
  baudRate: 115200,
  dataBits: 8,
  stopBits: 1,
);
```

## ⚠️ 常见问题

### 1. BLE 连接失败
- 检查设备是否支持 BLE
- 确保已申请 MTU（自动处理）
- 验证 UUID 是否正确

### 2. 经典蓝牙数据丢失
- 确保设备已配对
- 检查连接是否在后台线程执行
- 考虑降低发送频率

### 3. USB 设备无法识别
- 检查 OTG 是否启用
- 验证驱动程序（FTDI/CP210x/CH34x）
- 尝试重新插拔 USB

### 4. 数据粘包问题
- 使用定界符协议（推荐）
- 或实现定长协议
- 或自定义协议解析逻辑

## 📊 性能指标

| 指标 | BLE | 经典蓝牙 | USB |
|------|-----|---------|-----|
| 吞吐量 | 低 | 中 | 高 |
| 功耗 | 低 | 中 | 高 |
| 稳定性 | 中 | 高 | 很高 |
| 延迟 | 中 | 低 | 很低 |

## 🔐 安全建议

1. **权限管理**
   - 实现权限引导页
   - 动态申请权限
   - 处理权限拒绝

2. **数据安全**
   - 验证接收数据
   - 实现数据加密
   - 记录敏感操作日志

3. **错误处理**
   - 完善异常捕获
   - 提供用户友好的错误提示
   - 实现日志上报机制

## 📝 日志导出

应用支持一键导出日志，便于问题排查：
```dart
// 在数据监控页面点击"导出日志"按钮
// 日志将保存到本地存储
```

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

## 📄 许可证

MIT License

## 👨‍💻 作者

WuXBiao

---

**祝你硬件开发顺利！** 🚀
