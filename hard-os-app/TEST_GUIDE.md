# Hard OS App - 测试指南

## 🧪 单元测试

### 测试框架
使用 Flutter 内置的 `test` 和 `flutter_test` 框架。

### 创建测试文件

```bash
# 创建测试目录
mkdir test

# 创建测试文件
touch test/hardware_manager_test.dart
```

### 示例测试代码

```dart
import 'package:flutter_test/flutter_test.dart';
import 'package:hard_os_app/core/hardware_manager.dart';
import 'package:hard_os_app/core/models/connection_state.dart';

void main() {
  group('HardwareManager Tests', () {
    late HardwareManager manager;

    setUp(() {
      manager = HardwareManager();
    });

    tearDown(() {
      manager.dispose();
    });

    test('初始状态应该是未连接', () {
      expect(manager.currentState, ConnectionState.disconnected);
      expect(manager.isConnected, false);
    });

    test('创建 BLE 传输应该返回非空对象', () {
      final transport = manager.createBleTransport();
      expect(transport, isNotNull);
    });

    test('创建经典蓝牙传输应该返回非空对象', () {
      final transport = manager.createClassicBluetoothTransport();
      expect(transport, isNotNull);
    });

    test('创建 USB 传输应该返回非空对象', () {
      final transport = manager.createUsbTransport();
      expect(transport, isNotNull);
    });

    test('自动重连配置应该生效', () {
      manager.configureAutoReconnect(
        enabled: true,
        maxAttempts: 5,
        delay: const Duration(seconds: 3),
      );
      // 验证配置已保存
      expect(manager.currentState, ConnectionState.disconnected);
    });

    test('心跳包配置应该生效', () {
      manager.configureHeartbeat(
        enabled: true,
        interval: const Duration(seconds: 60),
        command: '0xFF',
      );
      // 验证配置已保存
      expect(manager.currentState, ConnectionState.disconnected);
    });
  });
}
```

### 运行测试

```bash
# 运行所有测试
flutter test

# 运行特定测试文件
flutter test test/hardware_manager_test.dart

# 运行特定测试用例
flutter test test/hardware_manager_test.dart -k "初始状态"

# 生成覆盖率报告
flutter test --coverage
```

## 🔍 集成测试

### 创建集成测试

```bash
mkdir integration_test
touch integration_test/app_test.dart
```

### 示例集成测试

```dart
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:hard_os_app/main.dart';

void main() {
  group('App Integration Tests', () {
    testWidgets('应用启动并显示主页', (WidgetTester tester) async {
      await tester.pumpWidget(const MyApp());
      
      // 验证应用标题
      expect(find.text('硬件通信应用'), findsOneWidget);
      
      // 验证主要组件存在
      expect(find.byType(AppBar), findsOneWidget);
      expect(find.byType(SingleChildScrollView), findsOneWidget);
    });

    testWidgets('连接状态卡片应该显示', (WidgetTester tester) async {
      await tester.pumpWidget(const MyApp());
      
      // 验证连接状态显示
      expect(find.text('连接状态:'), findsOneWidget);
      expect(find.text('未连接'), findsOneWidget);
    });

    testWidgets('设备扫描器应该显示', (WidgetTester tester) async {
      await tester.pumpWidget(const MyApp());
      
      // 验证设备扫描器
      expect(find.text('设备扫描'), findsOneWidget);
      expect(find.text('扫描设备'), findsOneWidget);
    });

    testWidgets('命令发送器应该显示', (WidgetTester tester) async {
      await tester.pumpWidget(const MyApp());
      
      // 验证命令发送器
      expect(find.text('命令发送'), findsOneWidget);
      expect(find.byType(TextField), findsOneWidget);
    });

    testWidgets('数据监控器应该显示', (WidgetTester tester) async {
      await tester.pumpWidget(const MyApp());
      
      // 验证数据监控器
      expect(find.text('数据监控'), findsOneWidget);
      expect(find.text('清空'), findsOneWidget);
    });
  });
}
```

### 运行集成测试

```bash
# 在 Android 设备上运行
flutter test integration_test/app_test.dart -d 22041211AC

# 在 iOS 设备上运行
flutter test integration_test/app_test.dart -d <device_id>

# 生成报告
flutter test integration_test/app_test.dart --verbose
```

## 📱 手动测试

### 测试场景 1：BLE 连接

1. 打开应用
2. 点击"选择传输方式"
3. 选择"BLE (低功耗蓝牙)"
4. 扫描附近的 BLE 设备
5. 选择一个设备进行连接
6. 验证连接状态变为"已连接"
7. 发送测试命令
8. 验证接收到响应

### 测试场景 2：经典蓝牙连接

1. 打开应用
2. 点击"选择传输方式"
3. 选择"经典蓝牙"
4. 扫描附近的蓝牙设备
5. 选择一个设备进行连接
6. 验证连接状态变为"已连接"
7. 发送测试命令
8. 验证接收到响应

### 测试场景 3：USB 连接

1. 使用 USB OTG 线连接 USB 设备
2. 打开应用
3. 点击"选择传输方式"
4. 选择"USB 串口"
5. 应用应该自动检测 USB 设备
6. 验证连接状态变为"已连接"
7. 发送测试命令
8. 验证接收到响应

### 测试场景 4：自动重连

1. 连接到设备
2. 断开设备电源或蓝牙
3. 验证应用自动尝试重连
4. 重新连接设备
5. 验证应用成功重新连接

### 测试场景 5：心跳包

1. 连接到设备
2. 启用心跳包
3. 观察数据监控面板
4. 验证定期发送心跳包
5. 断开设备
6. 验证心跳包停止发送

### 测试场景 6：数据粘包处理

1. 快速发送多个命令
2. 验证数据监控面板正确显示所有数据
3. 验证没有数据丢失或混乱

### 测试场景 7：命令模板

1. 打开应用
2. 点击"模板"按钮
3. 选择一个命令模板
4. 验证命令输入框被填充
5. 发送命令
6. 验证响应

### 测试场景 8：统计信息

1. 发送多个命令
2. 点击"统计信息"按钮
3. 验证显示的统计数据
   - 发送字节数
   - 接收字节数
   - 发送命令数
   - 接收响应数
   - 平均延迟

## 🐛 调试技巧

### 启用详细日志

```dart
// 在 main.dart 中
import 'package:logger/logger.dart';

void main() {
  // 设置日志级别
  Logger.level = Level.debug;
  
  runApp(const MyApp());
}
```

### 使用 Flutter DevTools

```bash
# 启动 DevTools
flutter pub global activate devtools
devtools

# 在应用运行时连接
flutter run --devtools-server-address=localhost:9100
```

### 性能分析

```bash
# 启用性能分析
flutter run --profile

# 或使用 DevTools 的 Performance 标签
```

### 内存泄漏检测

```bash
# 启用内存检测
flutter run --enable-memory-checks
```

## 📊 测试覆盖率

### 生成覆盖率报告

```bash
# 运行测试并生成覆盖率
flutter test --coverage

# 查看覆盖率报告
# 在 coverage/lcov.info 文件中
```

### 目标覆盖率

- 核心逻辑：> 80%
- UI 组件：> 60%
- 整体项目：> 70%

## ✅ 测试检查清单

- [ ] 所有单元测试通过
- [ ] 所有集成测试通过
- [ ] 代码覆盖率 > 70%
- [ ] 没有 lint 警告
- [ ] 没有性能问题
- [ ] 没有内存泄漏
- [ ] 在真机上测试通过
- [ ] 在多个 Android 版本上测试
- [ ] 在多个设备上测试
- [ ] 网络不稳定环境下测试

## 🔗 相关资源

- [Flutter Testing Documentation](https://flutter.dev/docs/testing)
- [Flutter Test Package](https://pub.dev/packages/flutter_test)
- [Integration Testing Guide](https://flutter.dev/docs/testing/integration-tests)
- [DevTools Documentation](https://flutter.dev/docs/development/tools/devtools)

---

**祝测试顺利！** ✨
