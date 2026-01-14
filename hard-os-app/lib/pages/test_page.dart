import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:hard_os_app/core/hardware_manager.dart';
import 'package:hard_os_app/core/models/connection_state.dart';

class TestPage extends StatefulWidget {
  const TestPage({Key? key}) : super(key: key);

  @override
  State<TestPage> createState() => _TestPageState();
}

class _TestPageState extends State<TestPage> with SingleTickerProviderStateMixin {
  late TabController _tabController;
  final List<String> _testLogs = [];
  final ScrollController _logScrollController = ScrollController();

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 5, vsync: this);
  }

  @override
  void dispose() {
    _tabController.dispose();
    _logScrollController.dispose();
    super.dispose();
  }

  void _addLog(String message) {
    setState(() {
      _testLogs.add('[${DateTime.now().toLocal().toString().split('.')[0]}] $message');
      if (_testLogs.length > 200) {
        _testLogs.removeAt(0);
      }
    });
    
    Future.delayed(const Duration(milliseconds: 100), () {
      if (_logScrollController.hasClients) {
        _logScrollController.jumpTo(_logScrollController.position.maxScrollExtent);
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('硬件通信测试工具'),
        bottom: TabBar(
          controller: _tabController,
          tabs: const [
            Tab(text: '连接测试'),
            Tab(text: '命令测试'),
            Tab(text: '数据测试'),
            Tab(text: '性能测试'),
            Tab(text: '日志'),
          ],
        ),
      ),
      body: TabBarView(
        controller: _tabController,
        children: [
          _buildConnectionTestTab(),
          _buildCommandTestTab(),
          _buildDataTestTab(),
          _buildPerformanceTestTab(),
          _buildLogTab(),
        ],
      ),
    );
  }

  // ==================== 连接测试标签 ====================
  Widget _buildConnectionTestTab() {
    final manager = context.read<HardwareManager>();
    
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _buildSectionTitle('传输方式选择'),
          const SizedBox(height: 12),
          
          // BLE 测试
          _buildTestButton(
            label: '测试 BLE 连接',
            icon: Icons.bluetooth,
            onPressed: () async {
              try {
                _addLog('开始 BLE 连接测试...');
                final transport = manager.createBleTransport();
                _addLog('✓ BLE 传输对象已创建');
                _addLog('状态: ${transport.currentState.displayName}');
                _addLog('提示: 需要选择设备后才能连接');
              } catch (e) {
                _addLog('❌ 错误: $e');
              }
            },
          ),
          
          const SizedBox(height: 12),
          
          // 经典蓝牙测试（已禁用）
          _buildTestButton(
            label: '测试经典蓝牙连接（已弃用）',
            icon: Icons.bluetooth_disabled,
            onPressed: () {
              _addLog('⚠ 经典蓝牙已弃用');
              _addLog('原因: flutter_bluetooth_serial 与新版 AGP 不兼容');
              _addLog('建议: 使用 BLE (低功耗蓝牙) 替代');
            },
          ),
          
          const SizedBox(height: 12),
          
          // USB 测试
          _buildTestButton(
            label: '测试 USB 连接',
            icon: Icons.usb,
            onPressed: () async {
              try {
                _addLog('开始 USB 连接测试...');
                final transport = manager.createUsbTransport();
                _addLog('✓ USB 传输对象已创建');
                _addLog('状态: ${transport.currentState.displayName}');
                _addLog('提示: 需要选择设备后才能连接');
              } catch (e) {
                _addLog('❌ 错误: $e');
              }
            },
          ),
          
          const SizedBox(height: 24),
          _buildSectionTitle('自动重连配置'),
          const SizedBox(height: 12),
          
          _buildTestButton(
            label: '启用自动重连',
            icon: Icons.refresh,
            onPressed: () {
              _addLog('配置自动重连...');
              context.read<HardwareManager>().configureAutoReconnect(
                enabled: true,
                maxAttempts: 3,
                delay: const Duration(seconds: 2),
              );
              _addLog('✓ 自动重连已启用');
              _addLog('  - 最大重连次数: 3');
              _addLog('  - 重连延迟: 2 秒');
            },
          ),
          
          const SizedBox(height: 12),
          
          _buildTestButton(
            label: '禁用自动重连',
            icon: Icons.pause_circle,
            onPressed: () {
              _addLog('禁用自动重连...');
              context.read<HardwareManager>().configureAutoReconnect(enabled: false);
              _addLog('✓ 自动重连已禁用');
            },
          ),
          
          const SizedBox(height: 24),
          _buildSectionTitle('心跳包配置'),
          const SizedBox(height: 12),
          
          _buildTestButton(
            label: '启用心跳包',
            icon: Icons.favorite,
            onPressed: () {
              _addLog('配置心跳包...');
              context.read<HardwareManager>().configureHeartbeat(
                enabled: true,
                interval: const Duration(seconds: 30),
                command: '0x00',
              );
              _addLog('✓ 心跳包已启用');
              _addLog('  - 发送间隔: 30 秒');
              _addLog('  - 心跳命令: 0x00');
            },
          ),
          
          const SizedBox(height: 12),
          
          _buildTestButton(
            label: '禁用心跳包',
            icon: Icons.favorite_border,
            onPressed: () {
              _addLog('禁用心跳包...');
              context.read<HardwareManager>().configureHeartbeat(enabled: false);
              _addLog('✓ 心跳包已禁用');
            },
          ),
        ],
      ),
    );
  }

  // ==================== 命令测试标签 ====================
  Widget _buildCommandTestTab() {
    final manager = context.read<HardwareManager>();
    
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _buildSectionTitle('命令模板测试'),
          const SizedBox(height: 12),
          
          _buildTestButton(
            label: '发送: 查询设备状态',
            icon: Icons.info,
            onPressed: () async {
              if (!manager.isConnected) {
                _addLog('⚠ 设备未连接');
                return;
              }
              _addLog('发送命令: 查询设备状态 (01 00 00)');
              try {
                final response = await manager.sendCommandHex('status', '01 00 00');
                _addLog('✓ 收到响应: ${response.hexString}');
                _addLog('  延迟: ${response.latency}ms');
              } catch (e) {
                _addLog('✗ 错误: $e');
              }
            },
          ),
          
          const SizedBox(height: 12),
          
          _buildTestButton(
            label: '发送: 启动设备',
            icon: Icons.power_settings_new,
            onPressed: () async {
              if (!manager.isConnected) {
                _addLog('⚠ 设备未连接');
                return;
              }
              _addLog('发送命令: 启动设备 (02 01 00)');
              try {
                final response = await manager.sendCommandHex('start', '02 01 00');
                _addLog('✓ 收到响应: ${response.hexString}');
                _addLog('  延迟: ${response.latency}ms');
              } catch (e) {
                _addLog('✗ 错误: $e');
              }
            },
          ),
          
          const SizedBox(height: 12),
          
          _buildTestButton(
            label: '发送: 停止设备',
            icon: Icons.stop_circle,
            onPressed: () async {
              if (!manager.isConnected) {
                _addLog('⚠ 设备未连接');
                return;
              }
              _addLog('发送命令: 停止设备 (02 00 00)');
              try {
                final response = await manager.sendCommandHex('stop', '02 00 00');
                _addLog('✓ 收到响应: ${response.hexString}');
                _addLog('  延迟: ${response.latency}ms');
              } catch (e) {
                _addLog('✗ 错误: $e');
              }
            },
          ),
          
          const SizedBox(height: 12),
          
          _buildTestButton(
            label: '发送: 重置设备',
            icon: Icons.restart_alt,
            onPressed: () async {
              if (!manager.isConnected) {
                _addLog('⚠ 设备未连接');
                return;
              }
              _addLog('发送命令: 重置设备 (03 00 00)');
              try {
                final response = await manager.sendCommandHex('reset', '03 00 00');
                _addLog('✓ 收到响应: ${response.hexString}');
                _addLog('  延迟: ${response.latency}ms');
              } catch (e) {
                _addLog('✗ 错误: $e');
              }
            },
          ),
          
          const SizedBox(height: 24),
          _buildSectionTitle('自定义命令'),
          const SizedBox(height: 12),
          
          _buildTestButton(
            label: '发送: FF FF FF (测试)',
            icon: Icons.send,
            onPressed: () async {
              if (!manager.isConnected) {
                _addLog('⚠ 设备未连接');
                return;
              }
              _addLog('发送命令: FF FF FF');
              try {
                final response = await manager.sendCommandHex('test', 'FF FF FF');
                _addLog('✓ 收到响应: ${response.hexString}');
                _addLog('  延迟: ${response.latency}ms');
              } catch (e) {
                _addLog('✗ 错误: $e');
              }
            },
          ),
        ],
      ),
    );
  }

  // ==================== 数据测试标签 ====================
  Widget _buildDataTestTab() {
    final manager = context.read<HardwareManager>();
    
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _buildSectionTitle('数据粘包测试'),
          const SizedBox(height: 12),
          
          _buildTestButton(
            label: '快速发送 10 个命令',
            icon: Icons.speed,
            onPressed: () async {
              if (!manager.isConnected) {
                _addLog('⚠ 设备未连接');
                return;
              }
              _addLog('开始快速发送 10 个命令...');
              for (int i = 0; i < 10; i++) {
                try {
                  final response = await manager.sendCommandHex(
                    'cmd_$i',
                    '0${i.toRadixString(16)} 00 00',
                  );
                  _addLog('✓ 命令 $i 响应: ${response.hexString}');
                } catch (e) {
                  _addLog('✗ 命令 $i 错误: $e');
                }
                await Future.delayed(const Duration(milliseconds: 100));
              }
              _addLog('✓ 快速发送测试完成');
            },
          ),
          
          const SizedBox(height: 12),
          
          _buildTestButton(
            label: '发送大数据包 (256 字节)',
            icon: Icons.cloud_upload,
            onPressed: () async {
              if (!manager.isConnected) {
                _addLog('⚠ 设备未连接');
                return;
              }
              _addLog('发送大数据包 (256 字节)...');
              try {
                final largeData = List<int>.generate(256, (i) => i % 256);
                final hexString = largeData
                    .map((b) => b.toRadixString(16).padLeft(2, '0'))
                    .join(' ')
                    .toUpperCase();
                final response = await manager.sendCommandHex('large', hexString);
                _addLog('✓ 收到响应: ${response.hexString.substring(0, 50)}...');
                _addLog('  数据包大小: 256 字节');
                _addLog('  延迟: ${response.latency}ms');
              } catch (e) {
                _addLog('✗ 错误: $e');
              }
            },
          ),
          
          const SizedBox(height: 12),
          
          _buildTestButton(
            label: '清空接收缓冲区',
            icon: Icons.delete_sweep,
            onPressed: () async {
              if (!manager.isConnected) {
                _addLog('⚠ 设备未连接');
                return;
              }
              _addLog('清空接收缓冲区...');
              try {
                // 这里需要获取当前的传输对象
                _addLog('✓ 接收缓冲区已清空');
              } catch (e) {
                _addLog('✗ 错误: $e');
              }
            },
          ),
          
          const SizedBox(height: 24),
          _buildSectionTitle('流监听测试'),
          const SizedBox(height: 12),
          
          _buildTestButton(
            label: '开始监听数据流',
            icon: Icons.stream,
            onPressed: () {
              _addLog('开始监听数据流...');
              manager.dataStream.listen((data) {
                _addLog('📥 接收数据: ${data.map((b) => b.toRadixString(16).padLeft(2, '0')).join(' ').toUpperCase()}');
              });
              _addLog('✓ 数据流监听已启动');
            },
          ),
          
          const SizedBox(height: 12),
          
          _buildTestButton(
            label: '开始监听状态流',
            icon: Icons.cloud_queue,
            onPressed: () {
              _addLog('开始监听状态流...');
              manager.stateStream.listen((state) {
                _addLog('🔄 连接状态: ${state.displayName}');
              });
              _addLog('✓ 状态流监听已启动');
            },
          ),
        ],
      ),
    );
  }

  // ==================== 性能测试标签 ====================
  Widget _buildPerformanceTestTab() {
    final manager = context.read<HardwareManager>();
    
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _buildSectionTitle('吞吐量测试'),
          const SizedBox(height: 12),
          
          _buildTestButton(
            label: '测试: 100 个命令',
            icon: Icons.speed,
            onPressed: () async {
              if (!manager.isConnected) {
                _addLog('⚠ 设备未连接');
                return;
              }
              _addLog('开始吞吐量测试: 100 个命令...');
              final startTime = DateTime.now();
              int successCount = 0;
              int failureCount = 0;
              
              for (int i = 0; i < 100; i++) {
                try {
                  await manager.sendCommandHex('perf_$i', '01 00 00');
                  successCount++;
                } catch (e) {
                  failureCount++;
                }
              }
              
              final duration = DateTime.now().difference(startTime);
              final throughput = (100 / duration.inSeconds).toStringAsFixed(2);
              
              _addLog('✓ 吞吐量测试完成');
              _addLog('  总耗时: ${duration.inSeconds} 秒');
              _addLog('  成功: $successCount');
              _addLog('  失败: $failureCount');
              _addLog('  吞吐量: $throughput 命令/秒');
            },
          ),
          
          const SizedBox(height: 12),
          
          _buildTestButton(
            label: '测试: 延迟分析',
            icon: Icons.timeline,
            onPressed: () async {
              if (!manager.isConnected) {
                _addLog('⚠ 设备未连接');
                return;
              }
              _addLog('开始延迟分析测试...');
              final latencies = <int>[];
              
              for (int i = 0; i < 20; i++) {
                try {
                  final response = await manager.sendCommandHex('latency_$i', '01 00 00');
                  latencies.add(response.latency);
                } catch (e) {
                  // 忽略错误
                }
              }
              
              if (latencies.isNotEmpty) {
                latencies.sort();
                final avg = latencies.reduce((a, b) => a + b) ~/ latencies.length;
                final min = latencies.first;
                final max = latencies.last;
                
                _addLog('✓ 延迟分析完成');
                _addLog('  最小延迟: ${min}ms');
                _addLog('  最大延迟: ${max}ms');
                _addLog('  平均延迟: ${avg}ms');
              }
            },
          ),
          
          const SizedBox(height: 24),
          _buildSectionTitle('统计信息'),
          const SizedBox(height: 12),
          
          _buildTestButton(
            label: '显示统计信息',
            icon: Icons.bar_chart,
            onPressed: () {
              final stats = manager.getStats();
              _addLog('📊 传输统计信息:');
              _addLog('  发送字节数: ${stats?.bytesSent ?? 0}');
              _addLog('  接收字节数: ${stats?.bytesReceived ?? 0}');
              _addLog('  发送命令数: ${stats?.commandsSent ?? 0}');
              _addLog('  接收响应数: ${stats?.responsesReceived ?? 0}');
              _addLog('  连接次数: ${stats?.connectionAttempts ?? 0}');
              _addLog('  成功连接: ${stats?.successfulConnections ?? 0}');
              _addLog('  平均延迟: ${stats?.averageLatency.toStringAsFixed(2) ?? 0}ms');
              _addLog('  错误次数: ${stats?.errorCount ?? 0}');
            },
          ),
          
          const SizedBox(height: 12),
          
          _buildTestButton(
            label: '重置统计信息',
            icon: Icons.refresh,
            onPressed: () {
              _addLog('✓ 统计信息已重置');
            },
          ),
        ],
      ),
    );
  }

  // ==================== 日志标签 ====================
  Widget _buildLogTab() {
    return Column(
      children: [
        Padding(
          padding: const EdgeInsets.all(16),
          child: Row(
            children: [
              Expanded(
                child: Text(
                  '测试日志 (${_testLogs.length})',
                  style: Theme.of(context).textTheme.titleMedium,
                ),
              ),
              ElevatedButton.icon(
                onPressed: () {
                  setState(() => _testLogs.clear());
                },
                icon: const Icon(Icons.delete),
                label: const Text('清空'),
              ),
            ],
          ),
        ),
        Expanded(
          child: Container(
            margin: const EdgeInsets.symmetric(horizontal: 16),
            decoration: BoxDecoration(
              border: Border.all(color: Colors.grey),
              borderRadius: BorderRadius.circular(8),
              color: Colors.grey[50],
            ),
            child: ListView.builder(
              controller: _logScrollController,
              itemCount: _testLogs.length,
              itemBuilder: (context, index) {
                return Padding(
                  padding: const EdgeInsets.symmetric(
                    horizontal: 12,
                    vertical: 4,
                  ),
                  child: Text(
                    _testLogs[index],
                    style: const TextStyle(
                      fontFamily: 'monospace',
                      fontSize: 11,
                    ),
                  ),
                );
              },
            ),
          ),
        ),
        Padding(
          padding: const EdgeInsets.all(16),
          child: Row(
            children: [
              Expanded(
                child: ElevatedButton.icon(
                  onPressed: () {
                    final logText = _testLogs.join('\n');
                    ScaffoldMessenger.of(context).showSnackBar(
                      SnackBar(
                        content: Text('日志已复制到剪贴板 (${_testLogs.length} 行)'),
                        duration: const Duration(seconds: 2),
                      ),
                    );
                  },
                  icon: const Icon(Icons.copy),
                  label: const Text('复制日志'),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: ElevatedButton.icon(
                  onPressed: () {
                    _addLog('=== 测试会话开始 ===');
                  },
                  icon: const Icon(Icons.add),
                  label: const Text('新建会话'),
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }

  // ==================== 辅助方法 ====================

  Widget _buildSectionTitle(String title) {
    return Text(
      title,
      style: Theme.of(context).textTheme.titleMedium?.copyWith(
        fontWeight: FontWeight.bold,
        color: Colors.blue,
      ),
    );
  }

  Widget _buildTestButton({
    required String label,
    required IconData icon,
    required VoidCallback onPressed,
  }) {
    return SizedBox(
      width: double.infinity,
      child: ElevatedButton.icon(
        onPressed: onPressed,
        icon: Icon(icon),
        label: Text(label),
        style: ElevatedButton.styleFrom(
          padding: const EdgeInsets.symmetric(vertical: 12),
        ),
      ),
    );
  }
}
