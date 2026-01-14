# Hard OS App - 快速开始指南

## ⚡ 5 分钟快速开始

### 1️⃣ 安装依赖（1 分钟）

```bash
cd d:\myWebSite\hard-os-app
flutter pub get
```

### 2️⃣ 运行应用（2 分钟）

```bash
# 在 Android 真机上运行
flutter run -d 22041211AC

# 或在 Windows 桌面上运行
flutter run -d windows

# 或在 Chrome 浏览器上运行
flutter run -d chrome
```

### 3️⃣ 打开测试工具（1 分钟）

1. 应用启动后，点击右上角的 **🐛 测试工具** 按钮
2. 进入测试工具页面

### 4️⃣ 运行测试（1 分钟）

- 点击 **连接测试** 标签
- 点击 **测试 BLE 连接** 按钮
- 查看日志验证成功

---

## 🎯 核心功能速览

### 连接管理
```dart
final manager = HardwareManager();

// 创建传输
final transport = manager.createBleTransport();

// 连接设备
await manager.connect(transport, deviceId);

// 断开连接
await manager.disconnect();
```

### 发送命令
```dart
// 发送十六进制命令
final response = await manager.sendCommandHex('cmd1', '01 02 03');

// 发送字符串命令
final response = await manager.sendCommandString('cmd2', 'hello');

// 发送字节数组
final response = await manager.sendCommand('cmd3', [0x01, 0x02, 0x03]);
```

### 监听事件
```dart
// 监听连接状态
manager.stateStream.listen((state) {
  print('连接状态: ${state.displayName}');
});

// 监听接收数据
manager.dataStream.listen((data) {
  print('接收数据: $data');
});

// 监听命令响应
manager.responseStream.listen((response) {
  print('响应: ${response.hexString}');
});
```

### 配置选项
```dart
// 自动重连
manager.configureAutoReconnect(
  enabled: true,
  maxAttempts: 3,
  delay: const Duration(seconds: 2),
);

// 心跳包
manager.configureHeartbeat(
  enabled: true,
  interval: const Duration(seconds: 30),
  command: '0x00',
);
```

---

## 📱 测试工具快速参考

### 连接测试
| 按钮 | 功能 |
|------|------|
| 测试 BLE 连接 | 创建 BLE 传输对象 |
| 测试经典蓝牙连接 | 创建经典蓝牙传输对象 |
| 测试 USB 连接 | 创建 USB 传输对象 |
| 启用自动重连 | 配置自动重连 |
| 启用心跳包 | 配置心跳包 |

### 命令测试
| 按钮 | 命令 |
|------|------|
| 查询设备状态 | 01 00 00 |
| 启动设备 | 02 01 00 |
| 停止设备 | 02 00 00 |
| 重置设备 | 03 00 00 |

### 数据测试
| 按钮 | 功能 |
|------|------|
| 快速发送 10 个命令 | 测试数据粘包 |
| 发送大数据包 (256 字节) | 测试大数据传输 |
| 开始监听数据流 | 监听接收数据 |
| 开始监听状态流 | 监听连接状态 |

### 性能测试
| 按钮 | 功能 |
|------|------|
| 测试: 100 个命令 | 吞吐量测试 |
| 测试: 延迟分析 | 延迟分析 |
| 显示统计信息 | 显示传输统计 |

---

## 🔧 常用命令

```bash
# 清理构建
flutter clean

# 获取依赖
flutter pub get

# 运行测试
flutter test

# 代码分析
flutter analyze

# 构建 APK
flutter build apk --release

# 构建 AAB
flutter build appbundle --release

# 构建 iOS
flutter build ios --release

# 构建 Windows
flutter build windows --release

# 构建 Web
flutter build web --release
```

---

## 📊 项目结构

```
hard-os-app/
├── lib/
│   ├── core/              # 核心逻辑
│   ├── pages/             # 页面
│   ├── widgets/           # 组件
│   └── main.dart          # 入口
├── android/               # Android 配置
├── ios/                   # iOS 配置
├── test/                  # 单元测试
├── integration_test/      # 集成测试
├── pubspec.yaml           # 依赖配置
└── README.md              # 项目说明
```

---

## 🚀 下一步

### 基础用户
1. ✅ 运行应用
2. ✅ 打开测试工具
3. ✅ 运行连接测试
4. 📖 阅读 [README.md](README.md)

### 开发者
1. ✅ 运行应用
2. ✅ 运行测试套件 (`flutter test`)
3. ✅ 阅读 [IMPLEMENTATION_GUIDE.md](IMPLEMENTATION_GUIDE.md)
4. 📖 开始开发新功能

### 测试人员
1. ✅ 运行应用
2. ✅ 打开测试工具
3. ✅ 运行完整测试套件
4. 📖 阅读 [TEST_TOOL_GUIDE.md](TEST_TOOL_GUIDE.md)

### 部署人员
1. ✅ 运行所有测试
2. ✅ 构建发布版本
3. 📖 阅读 [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md)

---

## ❓ 常见问题

### Q: 应用启动很慢？
A: 首次启动需要编译，请耐心等待 2-5 分钟。

### Q: 测试工具在哪里？
A: 点击主页右上角的 **🐛** 按钮。

### Q: 如何发送自定义命令？
A: 进入 **命令测试** 标签，使用 **发送: FF FF FF (测试)** 按钮。

### Q: 如何查看统计信息？
A: 进入 **性能测试** 标签，点击 **显示统计信息** 按钮。

### Q: 如何导出日志？
A: 进入 **日志** 标签，点击 **复制日志** 按钮。

---

## 📚 文档导航

| 文档 | 用途 |
|------|------|
| [README.md](README.md) | 项目概述和功能介绍 |
| [QUICK_START.md](QUICK_START.md) | 快速开始指南（本文档） |
| [IMPLEMENTATION_GUIDE.md](IMPLEMENTATION_GUIDE.md) | 详细实现指南 |
| [TEST_TOOL_GUIDE.md](TEST_TOOL_GUIDE.md) | 测试工具使用指南 |
| [TEST_GUIDE.md](TEST_GUIDE.md) | 单元测试和集成测试指南 |
| [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md) | 部署和发布指南 |

---

## 🎓 学习路径

### 初级（1-2 小时）
- [ ] 阅读 README.md
- [ ] 运行应用
- [ ] 使用测试工具
- [ ] 查看日志

### 中级（2-4 小时）
- [ ] 阅读 IMPLEMENTATION_GUIDE.md
- [ ] 理解 HAL 架构
- [ ] 学习传输实现
- [ ] 运行单元测试

### 高级（4+ 小时）
- [ ] 阅读所有文档
- [ ] 自定义传输协议
- [ ] 扩展功能
- [ ] 优化性能

---

## 🆘 获取帮助

### 查看日志
1. 打开测试工具
2. 进入 **日志** 标签
3. 查看详细的操作日志

### 运行诊断
```bash
# 检查环境
flutter doctor

# 运行测试
flutter test

# 代码分析
flutter analyze
```

### 查看文档
- 📖 [IMPLEMENTATION_GUIDE.md](IMPLEMENTATION_GUIDE.md) - 实现细节
- 📖 [TEST_TOOL_GUIDE.md](TEST_TOOL_GUIDE.md) - 测试工具
- 📖 [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md) - 部署问题

---

## 🎉 祝你使用愉快！

有任何问题，欢迎查看相关文档或提交 Issue。

**Happy coding!** 🚀
