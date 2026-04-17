package com.recording.manager.util;

import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Apache Tika 音频时长测试类
 * 测试使用 Tika 获取各种音频格式的时长
 */
@SpringBootTest
public class AudioDurationTikaTest {

    /**
     * 测试 Tika 音频时长获取功能
     */
    @Test
    public void testAudioDurationWithTika() {
        System.out.println("=== Apache Tika 音频时长测试 ===");
        
        // 测试文件路径数组
        String[] testFiles = {
            "/Users/xubiaowu/projects/myWebsite/tingleme/server-java/src/test/resources/test.wav",
            "/Users/xubiaowu/projects/myWebsite/tingleme/server-java/src/test/resources/test.mp3"
        };
        
        for (String filePath : testFiles) {
            System.out.println("\n--- 测试文件: " + filePath + " ---");
            testSingleFile(filePath);
        }
    }
    
    /**
     * 测试单个文件的时长获取
     */
    private void testSingleFile(String filePath) {
        try {
            File file = new File(filePath);
            
            // 检查文件是否存在
            if (!file.exists()) {
                System.out.println("✗ 文件不存在");
                return;
            }
            
            System.out.println("文件大小: " + file.length() + " bytes");
            
            // 1. 使用 Tika 检测文件类型
            Tika tika = new Tika();
            String mimeType = tika.detect(file);
            System.out.println("MIME 类型: " + mimeType);
            
            // 2. 解析元数据
            Parser parser = new AutoDetectParser();
            Metadata metadata = new Metadata();
            BodyContentHandler handler = new BodyContentHandler(-1);
            ParseContext context = new ParseContext();
            
            try (InputStream stream = new FileInputStream(file)) {
                parser.parse(stream, handler, metadata, context);
            }
            
            // 3. 显示所有元数据
            System.out.println("\n所有元数据:");
            String[] names = metadata.names();
            for (String name : names) {
                System.out.println("  " + name + ": " + metadata.get(name));
            }
            
            // 4. 提取时长信息
            String duration = extractDurationFromMetadata(metadata);
            System.out.println("\n提取的时长: " + duration);
            
            // 5. 使用自定义方法获取格式化时长
            String formattedDuration = getAudioDurationWithTika(filePath);
            System.out.println("格式化时长: " + formattedDuration);
            
            System.out.println("✓ 测试完成");
            
        } catch (Exception e) {
            System.out.println("✗ 测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 使用 Tika 获取音频时长的主要方法
     */
    public String getAudioDurationWithTika(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return "未知";
            }
            
            Parser parser = new AutoDetectParser();
            Metadata metadata = new Metadata();
            BodyContentHandler handler = new BodyContentHandler(-1);
            ParseContext context = new ParseContext();
            
            try (InputStream stream = new FileInputStream(file)) {
                parser.parse(stream, handler, metadata, context);
            }
            
            // 提取时长
            String durationStr = extractDurationFromMetadata(metadata);
            if (durationStr == null || durationStr.equals("未知")) {
                return "未知";
            }
            
            // 转换为秒数并格式化
            double durationValue = Double.parseDouble(durationStr);
            if (durationValue > 10000) {
                // 如果是毫秒，转换为秒
                durationValue = durationValue / 1000;
            }
            
            int totalSeconds = (int) durationValue;
            if (totalSeconds <= 0) {
                return "未知";
            }
            
            int hours = totalSeconds / 3600;
            int minutes = (totalSeconds % 3600) / 60;
            int seconds = totalSeconds % 60;
            
            return hours > 0 
                ? String.format("%02d:%02d:%02d", hours, minutes, seconds)
                : String.format("%02d:%02d", minutes, seconds);
                
        } catch (Exception e) {
            System.err.println("Tika 解析失败: " + filePath + " - " + e.getMessage());
            return "未知";
        }
    }
    
    /**
     * 从元数据中提取时长信息
     */
    private String extractDurationFromMetadata(Metadata metadata) {
        // 尝试各种可能的时长元数据键
        String[] durationKeys = {
            "xmpDM:duration",
            "duration", 
            "meta:duration",
            "mediainfo:Duration",
            "audio:duration"
        };
        
        for (String key : durationKeys) {
            String value = metadata.get(key);
            if (value != null && !value.isEmpty()) {
                System.out.println("找到时长元数据 [" + key + "]: " + value);
                return value;
            }
        }
        
        System.out.println("未找到时长元数据");
        return "未知";
    }
    
    /**
     * 测试不同音频格式的 MIME 类型检测
     */
    @Test
    public void testMimeTypeDetection() {
        System.out.println("=== MIME 类型检测测试 ===");
        
        Tika tika = new Tika();
        
        // 测试各种音频文件的 MIME 类型
        String[] testFiles = {
            "/Users/xubiaowu/projects/myWebsite/tingleme/server-java/src/test/resources/test.wav",
            "/Users/xubiaowu/projects/myWebsite/tingleme/server-java/src/test/resources/test.mp3"
        };
        
        for (String filePath : testFiles) {
            try {
                File file = new File(filePath);
                if (file.exists()) {
                    String mimeType = tika.detect(file);
                    System.out.println(filePath + " -> " + mimeType);
                }
            } catch (Exception e) {
                System.out.println(filePath + " -> 检测失败: " + e.getMessage());
            }
        }
    }
    
    /**
     * 简单的功能测试
     */
    @Test
    public void testBasicFunctionality() {
        System.out.println("=== 基本功能测试 ===");
        
        // 测试不存在的文件
        String result = getAudioDurationWithTika("/nonexistent/file.wav");
        System.out.println("不存在文件测试结果: " + result);
        
        // 测试空文件路径
        result = getAudioDurationWithTika("");
        System.out.println("空路径测试结果: " + result);
        
        System.out.println("✓ 基本功能测试完成");
    }
}