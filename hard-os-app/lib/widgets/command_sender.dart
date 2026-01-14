import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:hard_os_app/core/hardware_manager.dart';

class CommandSenderWidget extends StatefulWidget {
  const CommandSenderWidget({Key? key}) : super(key: key);

  @override
  State<CommandSenderWidget> createState() => _CommandSenderWidgetState();
}

class _CommandSenderWidgetState extends State<CommandSenderWidget> {
  final _commandController = TextEditingController();
  bool _isSending = false;

  @override
  void dispose() {
    _commandController.dispose();
    super.dispose();
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
            Text(
              '命令发送',
              style: Theme.of(context).textTheme.titleMedium,
            ),
            const SizedBox(height: 16),
            TextField(
              controller: _commandController,
              decoration: InputDecoration(
                hintText: '输入十六进制命令 (如: 01 02 03)',
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(8),
                ),
                suffixIcon: IconButton(
                  icon: const Icon(Icons.clear),
                  onPressed: _commandController.clear,
                ),
              ),
              maxLines: 3,
            ),
            const SizedBox(height: 12),
            Row(
              children: [
                ElevatedButton.icon(
                  onPressed: manager.isConnected && !_isSending
                      ? () => _sendCommand(manager)
                      : null,
                  icon: const Icon(Icons.send),
                  label: Text(_isSending ? '发送中...' : '发送'),
                ),
                const SizedBox(width: 8),
                ElevatedButton.icon(
                  onPressed: () => _showCommandTemplates(),
                  icon: const Icon(Icons.list),
                  label: const Text('模板'),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  void _sendCommand(HardwareManager manager) async {
    if (_commandController.text.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('请输入命令')),
      );
      return;
    }

    setState(() => _isSending = true);

    try {
      final response = await manager.sendCommandHex(
        'cmd_${DateTime.now().millisecondsSinceEpoch}',
        _commandController.text,
      );

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('响应: ${response.hexString}'),
            duration: const Duration(seconds: 3),
          ),
        );
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('错误: $e'),
            backgroundColor: Colors.red,
          ),
        );
      }
    } finally {
      if (mounted) {
        setState(() => _isSending = false);
      }
    }
  }

  void _showCommandTemplates() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('命令模板'),
        content: SingleChildScrollView(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              _TemplateButton(
                label: '查询设备状态',
                command: '01 00 00',
                onTap: () {
                  _commandController.text = '01 00 00';
                  Navigator.pop(context);
                },
              ),
              _TemplateButton(
                label: '启动设备',
                command: '02 01 00',
                onTap: () {
                  _commandController.text = '02 01 00';
                  Navigator.pop(context);
                },
              ),
              _TemplateButton(
                label: '停止设备',
                command: '02 00 00',
                onTap: () {
                  _commandController.text = '02 00 00';
                  Navigator.pop(context);
                },
              ),
              _TemplateButton(
                label: '重置设备',
                command: '03 00 00',
                onTap: () {
                  _commandController.text = '03 00 00';
                  Navigator.pop(context);
                },
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _TemplateButton extends StatelessWidget {
  final String label;
  final String command;
  final VoidCallback onTap;

  const _TemplateButton({
    required this.label,
    required this.command,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return ListTile(
      title: Text(label),
      subtitle: Text(command),
      onTap: onTap,
    );
  }
}
