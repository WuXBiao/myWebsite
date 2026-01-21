package com.recording.manager.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 流媒体服务
 * 支持 HTTP Range 请求，实现大文件分片传输
 * 
 * 功能：
 * - 支持断点续传
 * - 支持拖动进度条跳转播放
 * - 支持大文件分片下载
 */
@Service
public class StreamingService {

    // 默认分片大小：1MB
    private static final long CHUNK_SIZE = 1024 * 1024;

    /**
     * 构建支持 Range 请求的响应
     * 
     * @param filePath 文件路径
     * @param rangeHeader Range 请求头
     * @param contentType 内容类型
     * @return ResponseEntity
     */
    public ResponseEntity<byte[]> buildRangeResponse(String filePath, String rangeHeader, String contentType) {
        try {
            Path path = Paths.get(filePath);
            Resource resource = new UrlResource(path.toUri());
            
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            long fileLength = resource.contentLength();
            
            // 解析 Range 请求头
            RangeInfo rangeInfo = parseRangeHeader(rangeHeader, fileLength);
            
            if (rangeInfo == null) {
                // 无 Range 请求，返回完整文件
                return buildFullResponse(filePath, fileLength, contentType);
            }
            
            // 有 Range 请求，返回部分内容
            return buildPartialResponse(filePath, rangeInfo, fileLength, contentType);
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 构建完整文件响应
     */
    private ResponseEntity<byte[]> buildFullResponse(String filePath, long fileLength, String contentType) 
            throws IOException {
        byte[] data = readFileRange(filePath, 0, fileLength - 1);
        
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, contentType);
        headers.set(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileLength));
        headers.set(HttpHeaders.ACCEPT_RANGES, "bytes");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(data);
    }

    /**
     * 构建部分内容响应（206 Partial Content）
     */
    private ResponseEntity<byte[]> buildPartialResponse(String filePath, RangeInfo rangeInfo, 
            long fileLength, String contentType) throws IOException {
        
        long start = rangeInfo.start;
        long end = rangeInfo.end;
        long contentLength = end - start + 1;
        
        byte[] data = readFileRange(filePath, start, end);
        
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, contentType);
        headers.set(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength));
        headers.set(HttpHeaders.ACCEPT_RANGES, "bytes");
        headers.set(HttpHeaders.CONTENT_RANGE, String.format("bytes %d-%d/%d", start, end, fileLength));
        
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .headers(headers)
                .body(data);
    }

    /**
     * 读取文件指定范围的字节
     */
    private byte[] readFileRange(String filePath, long start, long end) throws IOException {
        try (RandomAccessFile file = new RandomAccessFile(filePath, "r")) {
            long length = end - start + 1;
            byte[] data = new byte[(int) length];
            
            file.seek(start);
            file.readFully(data);
            
            return data;
        }
    }

    /**
     * 解析 Range 请求头
     * 格式: bytes=start-end 或 bytes=start- 或 bytes=-suffix
     */
    private RangeInfo parseRangeHeader(String rangeHeader, long fileLength) {
        if (rangeHeader == null || !rangeHeader.startsWith("bytes=")) {
            return null;
        }
        
        try {
            String range = rangeHeader.substring(6); // 去掉 "bytes="
            String[] parts = range.split("-");
            
            long start, end;
            
            if (parts.length == 2 && !parts[0].isEmpty() && !parts[1].isEmpty()) {
                // bytes=start-end
                start = Long.parseLong(parts[0]);
                end = Math.min(Long.parseLong(parts[1]), fileLength - 1);
            } else if (parts.length == 2 && !parts[0].isEmpty()) {
                // bytes=start-
                start = Long.parseLong(parts[0]);
                end = Math.min(start + CHUNK_SIZE - 1, fileLength - 1);
            } else if (parts.length == 2 && parts[0].isEmpty()) {
                // bytes=-suffix
                long suffix = Long.parseLong(parts[1]);
                start = Math.max(fileLength - suffix, 0);
                end = fileLength - 1;
            } else {
                return null;
            }
            
            if (start > end || start < 0 || end >= fileLength) {
                return null;
            }
            
            return new RangeInfo(start, end);
            
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Range 信息
     */
    private static class RangeInfo {
        final long start;
        final long end;
        
        RangeInfo(long start, long end) {
            this.start = start;
            this.end = end;
        }
    }
}
