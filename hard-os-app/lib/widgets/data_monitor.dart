import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:hard_os_app/core/hardware_manager.dart';

class DataMonitorWidget extends StatefulWidget {
  const DataMonitorWidget({Key? key}) : super(key: key);

  @override
  State<DataMonitorWidget> createState() => _DataMonitorWidgetState();
}

class _DataMonitorWidgetState extends State<DataMonitorWidget> {
  final List<String> _logs = [];
  final ScrollController _scrollController = ScrollController();

  @override
  void dispose() {
    _scrollController.dispose();
    super.dispose();
  }

  void _addLog(String message) {
    setState(() {
      _logs.add('[${DateTime.now().toLocal().toString().split('.')[0]}] $message');
      if (_logs.length > 100) {
        _logs.removeAt(0);
      }
    });
    
    // 自动滚动到底部
    Future.delayed(const Duration(milliseconds: 100), () {
      if (_scrollController.hasClients) {
        _scrollController.jumpTo(_scrollController.position.maxScrollExtent);
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    final manager = context.read<HardwareManager>();
    
    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 16),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(
                  '数据监控',
                  style: Theme.of(context).textTheme.titleMedium,
                ),
                ElevatedButton.icon(
                  onPressed: () {
                    setState(() => _logs.clear());
                  },
                  icon: const Icon(Icons.delete),
                  label: const Text('清空'),
                ),
              ],
            ),
            const SizedBox(height: 12),
            Container(
              height: 200,
              decoration: BoxDecoration(
                border: Border.all(color: Colors.grey),
                borderRadius: BorderRadius.circular(8),
                color: Colors.grey[50],
              ),
              child: StreamBuilder<List<int>>(
                stream: manager.dataStream,
                builder: (context, snapshot) {
                  if (snapshot.hasData) {
                    final data = snapshot.data!;
                    _addLog('接收: ${_bytesToHex(data)}');
                  }
                  
                  return ListView.builder(
                    controller: _scrollController,
                    itemCount: _logs.length,
                    itemBuilder: (context, index) {
                      return Padding(
                        padding: const EdgeInsets.symmetric(
                          horizontal: 8,
                          vertical: 4,
                        ),
                        child: Text(
                          _logs[index],
                          style: const TextStyle(
                            fontFamily: 'monospace',
                            fontSize: 12,
                          ),
                        ),
                      );
                    },
                  );
                },
              ),
            ),
            const SizedBox(height: 12),
            ElevatedButton.icon(
              onPressed: () => _showStats(manager),
              icon: const Icon(Icons.bar_chart),
              label: const Text('统计信息'),
            ),
          ],
        ),
      ),
    );
  }

  void _showStats(HardwareManager manager) {
    final stats = manager.getStats();
    
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('传输统计'),
        content: SingleChildScrollView(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              _StatItem('发送字节数', '${stats?.bytesSent ?? 0}'),
              _StatItem('接收字节数', '${stats?.bytesReceived ?? 0}'),
              _StatItem('发送命令数', '${stats?.commandsSent ?? 0}'),
              _StatItem('接收响应数', '${stats?.responsesReceived ?? 0}'),
              _StatItem('连接次数', '${stats?.connectionAttempts ?? 0}'),
              _StatItem('成功连接', '${stats?.successfulConnections ?? 0}'),
              _StatItem('平均延迟', '${stats?.averageLatency.toStringAsFixed(2) ?? 0}ms'),
              _StatItem('错误次数', '${stats?.errorCount ?? 0}'),
            ],
          ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('关闭'),
          ),
        ],
      ),
    );
  }

  String _bytesToHex(List<int> bytes) {
    return bytes.map((b) => b.toRadixString(16).padLeft(2, '0')).join(' ').toUpperCase();
  }
}

class _StatItem extends StatelessWidget {
  final String label;
  final String value;

  const _StatItem(this.label, this.value);

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(label),
          Text(
            value,
            style: const TextStyle(fontWeight: FontWeight.bold),
          ),
        ],
      ),
    );
  }
}
