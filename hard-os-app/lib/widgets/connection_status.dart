import 'package:flutter/material.dart';
import 'package:hard_os_app/core/models/connection_state.dart';

class ConnectionStatusWidget extends StatelessWidget {
  final HardwareConnectionState state;

  const ConnectionStatusWidget({
    Key? key,
    required this.state,
  }) : super(key: key);

  Color _getStatusColor() {
    switch (state) {
      case HardwareConnectionState.connected:
        return Colors.green;
      case HardwareConnectionState.connecting:
        return Colors.orange;
      case HardwareConnectionState.disconnected:
        return Colors.grey;
      case HardwareConnectionState.failed:
      case HardwareConnectionState.timeout:
        return Colors.red;
      default:
        return Colors.grey;
    }
  }

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.all(16),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            Row(
              children: [
                Container(
                  width: 12,
                  height: 12,
                  decoration: BoxDecoration(
                    color: _getStatusColor(),
                    shape: BoxShape.circle,
                  ),
                ),
                const SizedBox(width: 12),
                Text(
                  '连接状态: ${state.displayName}',
                  style: Theme.of(context).textTheme.titleMedium,
                ),
              ],
            ),
            const SizedBox(height: 8),
            Text(
              '${DateTime.now().toLocal()}',
              style: Theme.of(context).textTheme.bodySmall,
            ),
          ],
        ),
      ),
    );
  }
}
