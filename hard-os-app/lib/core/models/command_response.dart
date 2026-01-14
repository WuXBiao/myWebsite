/// 命令响应模型
class CommandResponse {
  /// 命令 ID
  final String commandId;
  
  /// 响应数据（字节数组）
  final List<int> data;
  
  /// 响应时间戳
  final DateTime timestamp;
  
  /// 是否成功
  final bool success;
  
  /// 错误信息
  final String? errorMessage;
  
  /// 原始十六进制字符串
  String get hexString => data.map((b) => b.toRadixString(16).padLeft(2, '0')).join(' ').toUpperCase();
  
  /// 响应延迟（毫秒）
  late final int latency;

  CommandResponse({
    required this.commandId,
    required this.data,
    required this.timestamp,
    this.success = true,
    this.errorMessage,
    int? latency,
  }) {
    this.latency = latency ?? 0;
  }

  /// 将数据转换为字符串
  String toAsciiString() {
    try {
      return String.fromCharCodes(data);
    } catch (e) {
      return '(非 ASCII 数据)';
    }
  }

  @override
  String toString() => 'CommandResponse(id: $commandId, success: $success, data: $hexString)';
}
