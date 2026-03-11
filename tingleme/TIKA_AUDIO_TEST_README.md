# Apache Tika 音频时长测试使用说明

## 测试类说明

`AudioDurationTikaTest.java` 提供了完整的 Apache Tika 音频时长获取测试功能。

## 主要测试方法

### 1. testAudioDurationWithTika()
- 测试 Tika 获取音频时长的核心功能
- 支持多种音频格式（WAV、MP3等）
- 显示详细的元数据信息

### 2. testMimeTypeDetection()
- 测试 Tika 的 MIME 类型检测功能
- 验证文件类型识别准确性

### 3. testBasicFunctionality()
- 测试异常情况处理
- 验证边界条件

## 使用方法

### 运行所有测试
```bash
cd /Users/xubiaowu/projects/myWebsite/tingleme/server-java
mvn test -Dtest=AudioDurationTikaTest
```

### 运行特定测试
```bash
# 只运行时长测试
mvn test -Dtest=AudioDurationTikaTest#testAudioDurationWithTika

# 只运行 MIME 类型测试
mvn test -Dtest=AudioDurationTikaTest#testMimeTypeDetection
```

## 测试文件准备

在运行测试前，请准备测试文件：
```
/Users/xubiaowu/projects/myWebsite/tingleme/server-java/src/test/resources/
├── test.wav
└── test.mp3
```

## 预期输出示例

```
=== Apache Tika 音频时长测试 ===

--- 测试文件: /path/to/test.wav ---
文件大小: 1024000 bytes
MIME 类型: audio/x-wav

所有元数据:
  xmpDM:duration: 120000
  duration: 120000
  meta:duration: 120000

找到时长元数据 [xmpDM:duration]: 120000
提取的时长: 120000

格式化时长: 02:00
✓ 测试完成
```

## 依赖说明

测试类依赖以下 Maven 依赖：
```xml
<dependency>
    <groupId>org.apache.tika</groupId>
    <artifactId>tika-core</artifactId>
    <version>2.9.1</version>
</dependency>
<dependency>
    <groupId>org.apache.tika</groupId>
    <artifactId>tika-parsers-standard-package</artifactId>
    <version>2.9.1</version>
</dependency>
```

## 注意事项

1. **性能考虑**：Tika 解析相对较慢，适合一次性处理
2. **准确性**：依赖于文件元数据，某些文件可能没有时长信息
3. **格式支持**：支持大多数常见音频格式
4. **错误处理**：已包含完善的异常处理机制

## 扩展建议

可以根据需要添加：
- 更多音频格式测试
- 性能基准测试
- 与其他库的对比测试
- 批量文件处理测试