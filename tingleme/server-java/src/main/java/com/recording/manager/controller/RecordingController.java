package com.recording.manager.controller;

import com.recording.manager.entity.Recording;
import com.recording.manager.service.RecordingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/recordings")
@CrossOrigin(origins = "*")
public class RecordingController {

    @Autowired
    private RecordingService recordingService;

    // 分页获取录音列表
    @GetMapping
    public ResponseEntity<Map<String, Object>> getRecordings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String uploader) {
        
        Sort sort = sortDir.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Recording> recordings;
        if (title != null || uploader != null) {
            recordings = recordingService.getRecordingsWithFilters(title, uploader, pageable);
        } else {
            recordings = recordingService.getAllRecordings(pageable);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("data", recordings.getContent());
        response.put("currentPage", recordings.getNumber());
        response.put("totalItems", recordings.getTotalElements());
        response.put("totalPages", recordings.getTotalPages());
        response.put("size", recordings.getSize());

        return ResponseEntity.ok(response);
    }

    // 获取单个录音信息
    @GetMapping("/{id}")
    public ResponseEntity<Recording> getRecordingById(@PathVariable Long id) {
        return recordingService.getRecordingById(id)
                .map(record -> ResponseEntity.ok().body(record))
                .orElse(ResponseEntity.notFound().build());
    }

    // 上传录音
    @PostMapping("/upload")
    public ResponseEntity<?> uploadRecording(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "uploader", required = false) String uploader) {
        
        try {
            Recording recording = recordingService.uploadRecording(file, title, description, uploader);
            return ResponseEntity.ok(recording);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("文件上传失败: " + e.getMessage());
        }
    }

    // 删除录音
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecording(@PathVariable Long id) {
        recordingService.deleteRecording(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "录音删除成功");
        return ResponseEntity.ok().body(response);
    }

    // 播放录音（支持两种 URL 格式）
    @GetMapping(value = {"/play/{id}", "/play/{id}/{fileName}"})
    public ResponseEntity<Resource> playRecording(@PathVariable Long id, HttpServletRequest request) {
        return recordingService.getRecordingById(id)
                .map(recording -> {
                    String filePath = recording.getFilePath();
                    Resource resource = new FileSystemResource(filePath);
                    
                    if (!resource.exists()) {
                        return ResponseEntity.notFound().<Resource>build();
                    }

                    // 根据文件扩展名确定 MIME 类型
                    String contentType = getAudioContentType(filePath);
                    
                    // 如果无法通过扩展名确定，尝试通过 ServletContext 获取
                    if (contentType == null) {
                        try {
                            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
                        } catch (IOException ex) {
                            // 忽略异常
                        }
                    }

                    // 默认为 MP3 格式
                    if (contentType == null) {
                        contentType = "audio/mpeg";
                    }

                    // 处理中文文件名
                    String fileName = recording.getFileName();
                    String encodedFileName;
                    try {
                        encodedFileName = URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
                    } catch (UnsupportedEncodingException e) {
                        encodedFileName = fileName;
                    }

                    return ResponseEntity.ok()
                            .contentType(MediaType.parseMediaType(contentType))
                            .header(HttpHeaders.CONTENT_DISPOSITION, 
                                    "inline; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName)
                            .body(resource);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 根据文件扩展名获取音频 MIME 类型
    private String getAudioContentType(String filePath) {
        if (filePath == null) return null;
        
        String lowerPath = filePath.toLowerCase();
        if (lowerPath.endsWith(".mp3")) {
            return "audio/mpeg";
        } else if (lowerPath.endsWith(".wav")) {
            return "audio/wav";
        } else if (lowerPath.endsWith(".ogg")) {
            return "audio/ogg";
        } else if (lowerPath.endsWith(".m4a")) {
            return "audio/mp4";
        } else if (lowerPath.endsWith(".aac")) {
            return "audio/aac";
        } else if (lowerPath.endsWith(".flac")) {
            return "audio/flac";
        } else if (lowerPath.endsWith(".wma")) {
            return "audio/x-ms-wma";
        } else if (lowerPath.endsWith(".webm")) {
            return "audio/webm";
        }
        return null;
    }

    // 下载录音
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadRecording(@PathVariable Long id) {
        return recordingService.getRecordingById(id)
                .map(recording -> {
                    String filePath = recording.getFilePath();
                    Resource resource = new FileSystemResource(filePath);
                    
                    if (!resource.exists()) {
                        return ResponseEntity.notFound().<Resource>build();
                    }
                    
                    String fileName = recording.getFileName();
                    String encodedFileName;
                    try {
                        encodedFileName = URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
                    } catch (UnsupportedEncodingException e) {
                        encodedFileName = fileName;
                    }
                    
                    return ResponseEntity.ok()
                            .contentType(MediaType.APPLICATION_OCTET_STREAM)
                            .header(HttpHeaders.CONTENT_DISPOSITION, 
                                    "attachment; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName)
                            .body(resource);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}