import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:flutter_blue_plus/flutter_blue_plus.dart';
import 'package:hard_os_app/core/hardware_manager.dart';
import 'package:hard_os_app/core/interfaces/hardware_transport.dart';

class DeviceScannerWidget extends StatefulWidget {
  const DeviceScannerWidget({Key? key}) : super(key: key);

  @override
  State<DeviceScannerWidget> createState() => _DeviceScannerWidgetState();
}

class _DeviceScannerWidgetState extends State<DeviceScannerWidget> {
  bool _isScanning = false;
  List<ScanResult> _scanResults = [];
  String? _selectedDeviceId;

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 16),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              '设备扫描',
              style: Theme.of(context).textTheme.titleMedium,
            ),
            const SizedBox(height: 16),
            Row(
              children: [
                ElevatedButton.icon(
                  onPressed: _isScanning ? null : _scanDevices,
                  icon: const Icon(Icons.search),
                  label: Text(_isScanning ? '扫描中...' : '扫描设备'),
                ),
                const SizedBox(width: 8),
                ElevatedButton.icon(
                  onPressed: _showDeviceOptions,
                  icon: const Icon(Icons.devices),
                  label: const Text('选择传输方式'),
                ),
              ],
            ),
            const SizedBox(height: 8),
            Text(
              '支持: BLE、经典蓝牙、USB 串口',
              style: Theme.of(context).textTheme.bodySmall,
            ),
          ],
        ),
      ),
    );
  }

  void _scanDevices() async {
    try {
      // 检查蓝牙状态
      final adapterState = await FlutterBluePlus.adapterState.first;
      if (adapterState != BluetoothAdapterState.on) {
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(
              content: Text('❌ 蓝牙未开启，请先打开蓝牙'),
              backgroundColor: Colors.red,
            ),
          );
        }
        return;
      }

      setState(() {
        _isScanning = true;
        _scanResults = [];
      });

      // 开始扫描
      await FlutterBluePlus.startScan(
        timeout: const Duration(seconds: 10),
      );

      // 监听扫描结果
      FlutterBluePlus.scanResults.listen((results) {
        if (mounted) {
          setState(() {
            _scanResults = results;
          });
        }
      });

      // 监听扫描状态
      FlutterBluePlus.isScanning.listen((isScanning) {
        if (mounted) {
          setState(() {
            _isScanning = isScanning;
          });
        }
      });

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('✓ 扫描完成，找到 ${_scanResults.length} 个设备'),
            backgroundColor: Colors.green,
          ),
        );
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('❌ 扫描失败: $e'),
            backgroundColor: Colors.red,
          ),
        );
      }
    }
  }

  void _showDeviceOptions() {
    final manager = context.read<HardwareManager>();
    
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('选择连接方式'),
        content: SingleChildScrollView(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              // BLE 设备列表
              if (_scanResults.isNotEmpty) ...[
                const Padding(
                  padding: EdgeInsets.symmetric(vertical: 8),
                  child: Text(
                    '扫描到的 BLE 设备',
                    style: TextStyle(fontWeight: FontWeight.bold),
                  ),
                ),
                ..._scanResults.map((result) => ListTile(
                  title: Text(result.device.platformName.isEmpty 
                    ? '未命名设备' 
                    : result.device.platformName),
                  subtitle: Text('信号强度: ${result.rssi} dBm'),
                  onTap: () {
                    Navigator.pop(context);
                    _connectBleDevice(
                      manager.createBleTransport(),
                      result.device.remoteId.toString(),
                    );
                  },
                )),
                const Divider(),
              ] else ...[
                const Padding(
                  padding: EdgeInsets.all(8),
                  child: Text(
                    '未找到 BLE 设备\n请先点击"扫描设备"',
                    textAlign: TextAlign.center,
                    style: TextStyle(color: Colors.grey),
                  ),
                ),
                const Divider(),
              ],
              
              // 传输方式选择
              const Padding(
                padding: EdgeInsets.symmetric(vertical: 8),
                child: Text(
                  '其他传输方式',
                  style: TextStyle(fontWeight: FontWeight.bold),
                ),
              ),
              ListTile(
                title: const Text('USB 串口'),
                subtitle: const Text('最稳定，适合工业场景'),
                onTap: () {
                  Navigator.pop(context);
                  _connectDevice(manager.createUsbTransport());
                },
              ),
            ],
          ),
        ),
      ),
    );
  }

  void _connectBleDevice(IHardwareTransport transport, String deviceId) async {
    try {
      final manager = context.read<HardwareManager>();
      
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('正在连接 BLE 设备: $deviceId')),
      );
      
      bool success = await manager.connect(transport, deviceId);
      
      if (success) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('✓ BLE 设备已连接'),
            backgroundColor: Colors.green,
          ),
        );
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('❌ BLE 连接失败'),
            backgroundColor: Colors.red,
          ),
        );
      }
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('❌ 错误: $e'),
          backgroundColor: Colors.red,
        ),
      );
    }
  }

  void _connectDevice(IHardwareTransport transport) async {
    try {
      final manager = context.read<HardwareManager>();
      
      // 显示连接中的提示
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('正在连接 ${transport.runtimeType}...')),
      );
      
      // 使用模拟设备 ID 进行连接
      // 实际应用中应该从扫描结果中获取真实的设备 ID
      final deviceId = 'mock_device_${DateTime.now().millisecondsSinceEpoch}';
      
      bool success = await manager.connect(transport, deviceId);
      
      if (success) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('✓ 已连接: ${transport.runtimeType}'),
            backgroundColor: Colors.green,
          ),
        );
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('❌ 连接失败: ${transport.runtimeType}'),
            backgroundColor: Colors.red,
          ),
        );
      }
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('❌ 错误: $e'),
          backgroundColor: Colors.red,
        ),
      );
    }
  }
}
