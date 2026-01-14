import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:hard_os_app/core/hardware_manager.dart';
import 'package:hard_os_app/core/models/connection_state.dart';
import 'package:hard_os_app/pages/test_page.dart';
import 'package:hard_os_app/widgets/device_scanner.dart';
import 'package:hard_os_app/widgets/connection_status.dart';
import 'package:hard_os_app/widgets/command_sender.dart';
import 'package:hard_os_app/widgets/data_monitor.dart';

class HomePage extends StatefulWidget {
  const HomePage({Key? key}) : super(key: key);

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> with WidgetsBindingObserver {
  late HardwareManager _hardwareManager;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
    _hardwareManager = context.read<HardwareManager>();
    
    // 配置自动重连
    _hardwareManager.configureAutoReconnect(
      enabled: true,
      maxAttempts: 3,
      delay: const Duration(seconds: 2),
    );
    
    // 配置心跳包
    _hardwareManager.configureHeartbeat(
      enabled: true,
      interval: const Duration(seconds: 30),
      command: '0x00',
    );
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    if (state == AppLifecycleState.paused) {
      // 应用进入后台
    } else if (state == AppLifecycleState.resumed) {
      // 应用返回前台
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('硬件通信应用'),
        elevation: 0,
        actions: [
          IconButton(
            icon: const Icon(Icons.bug_report),
            tooltip: '测试工具',
            onPressed: () {
              Navigator.of(context).push(
                MaterialPageRoute(builder: (_) => const TestPage()),
              );
            },
          ),
        ],
      ),
      body: SingleChildScrollView(
        child: Column(
          children: [
            // 连接状态卡片
            StreamBuilder<HardwareConnectionState>(
              stream: _hardwareManager.stateStream,
              initialData: _hardwareManager.currentState,
              builder: (context, snapshot) {
                final state = snapshot.data ?? HardwareConnectionState.disconnected;
                return ConnectionStatusWidget(state: state);
              },
            ),
            
            const SizedBox(height: 16),
            
            // 设备扫描器
            const DeviceScannerWidget(),
            
            const SizedBox(height: 16),
            
            // 命令发送器
            const CommandSenderWidget(),
            
            const SizedBox(height: 16),
            
            // 数据监控器
            const DataMonitorWidget(),
            
            const SizedBox(height: 16),
          ],
        ),
      ),
    );
  }
}
