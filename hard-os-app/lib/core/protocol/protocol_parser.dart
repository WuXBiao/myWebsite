import 'dart:async';

/// 协议解析器
/// 处理数据粘包和分包问题
abstract class ProtocolParser {
  /// 解析接收到的数据
  /// 返回完整的数据包列表
  List<List<int>> parse(List<int> data);
  
  /// 重置解析器状态
  void reset();
}

/// 定界符协议解析器
/// 使用 STX (0x02) 和 ETX (0x03) 作为数据包的开始和结束标记
class DelimiterProtocolParser implements ProtocolParser {
  static const int STX = 0x02; // 数据包开始标记
  static const int ETX = 0x03; // 数据包结束标记
  
  final List<int> _buffer = [];
  final List<List<int>> _packets = [];

  @override
  List<List<int>> parse(List<int> data) {
    _packets.clear();
    _buffer.addAll(data);
    
    // 查找完整的数据包
    while (_buffer.isNotEmpty) {
      int stxIndex = _buffer.indexOf(STX);
      
      if (stxIndex == -1) {
        // 没有找到 STX，清空缓冲区
        _buffer.clear();
        break;
      }
      
      if (stxIndex > 0) {
        // 移除 STX 之前的数据
        _buffer.removeRange(0, stxIndex);
      }
      
      // 查找 ETX
      int etxIndex = _buffer.indexOf(ETX);
      
      if (etxIndex == -1) {
        // 没有找到 ETX，等待更多数据
        break;
      }
      
      // 提取完整的数据包（包含 STX 和 ETX）
      List<int> packet = _buffer.sublist(0, etxIndex + 1);
      _packets.add(packet);
      
      // 移除已处理的数据包
      _buffer.removeRange(0, etxIndex + 1);
    }
    
    return _packets;
  }

  @override
  void reset() {
    _buffer.clear();
    _packets.clear();
  }
}

/// 定长协议解析器
/// 每个数据包长度固定
class FixedLengthProtocolParser implements ProtocolParser {
  final int packetLength;
  final List<int> _buffer = [];
  final List<List<int>> _packets = [];

  FixedLengthProtocolParser({required this.packetLength});

  @override
  List<List<int>> parse(List<int> data) {
    _packets.clear();
    _buffer.addAll(data);
    
    // 提取完整的数据包
    while (_buffer.length >= packetLength) {
      List<int> packet = _buffer.sublist(0, packetLength);
      _packets.add(packet);
      _buffer.removeRange(0, packetLength);
    }
    
    return _packets;
  }

  @override
  void reset() {
    _buffer.clear();
    _packets.clear();
  }
}

/// 自定义协议解析器
/// 允许用户定义自己的解析逻辑
class CustomProtocolParser implements ProtocolParser {
  final List<int> Function(List<int>) _parseFunction;
  final List<int> _buffer = [];

  CustomProtocolParser({required List<int> Function(List<int>) parseFunction})
      : _parseFunction = parseFunction;

  @override
  List<List<int>> parse(List<int> data) {
    _buffer.addAll(data);
    List<int> result = _parseFunction(_buffer);
    
    if (result.isNotEmpty) {
      _buffer.clear();
      return [result];
    }
    
    return [];
  }

  @override
  void reset() {
    _buffer.clear();
  }
}

/// 无协议解析器
/// 直接返回接收到的数据，不进行任何处理
class NoProtocolParser implements ProtocolParser {
  @override
  List<List<int>> parse(List<int> data) {
    return [data];
  }

  @override
  void reset() {
    // 无需重置
  }
}
