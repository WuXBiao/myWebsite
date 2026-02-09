package com.recording.manager.controller;

import com.recording.manager.dto.RecordingUploadRequest;
import com.recording.manager.entity.Recording;
import com.recording.manager.security.RequireRole;
import com.recording.manager.security.Role;
import com.recording.manager.service.RecordingService;
import com.recording.manager.service.UserRoleService;
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
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/recordings")
@CrossOrigin(origins = "*")
public class RecordingController {

    @Autowired
    private RecordingService recordingService;
    
    @Autowired
    private UserRoleService userRoleService;

    // 分页获取录音列表（USER 角色只能查看所属机构的数据）
    @GetMapping
    public ResponseEntity<Map<String, Object>> getRecordings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String uploader,
            HttpServletRequest request) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // 获取当前用户的工号和角色
        String employeeId = request.getHeader("X-Employee-Id");
        Role currentRole = userRoleService.getHighestRole(employeeId);
        
        Page<Recording> recordings;
        
        // 根据角色查询数据
        if (currentRole == Role.ADMIN) {
            // 管理员查看所有数据
            if (title != null || uploader != null) {
                recordings = recordingService.getRecordingsWithFilters(title, uploader, pageable);
            } else {
                recordings = recordingService.getAllRecordings(pageable);
            }
        } else if (currentRole == Role.UPLOADER) {
            // 上传者只能查看自己上传的录音
            if (title != null || uploader != null) {
                recordings = recordingService.getRecordingsByUploaderEmployeeIdWithFilters(employeeId, title, uploader, pageable);
            } else {
                recordings = recordingService.getRecordingsByUploaderEmployeeId(employeeId, pageable);
            }
        } else {
            // USER 角色只能查看所属机构的数据
            String orgCode = userRoleService.getOrgCode(employeeId);
            if (orgCode == null) {
                // 没有机构信息，返回空数据
                Map<String, Object> response = new HashMap<>();
                response.put("data", java.util.Collections.emptyList());
                response.put("currentPage", 0);
                response.put("totalItems", 0);
                response.put("totalPages", 0);
                response.put("size", size);
                return ResponseEntity.ok(response);
            }
            
            if (title != null || uploader != null) {
                recordings = recordingService.getRecordingsByOrgCodeWithFilters(orgCode, title, uploader, pageable);
            } else {
                recordings = recordingService.getRecordingsByOrgCode(orgCode, pageable);
            }
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

    // 上传录音（上传者、管理员可操作）
    @PostMapping("/upload")
    @RequireRole({Role.UPLOADER, Role.ADMIN})
    public ResponseEntity<?> uploadRecording(
            @RequestParam("file") MultipartFile file,
            @Valid @ModelAttribute RecordingUploadRequest request,
            HttpServletRequest httpRequest) {
        
        try {
            // 构建 Recording 对象
            Recording recording = new Recording();
            recording.setTitle(request.getTitle());
            recording.setDescription(request.getDescription());
            recording.setUploader(request.getUploader());
            
            // 设置上传者的机构编码和工号
            String employeeId = httpRequest.getHeader("X-Employee-Id");
            String orgCode = userRoleService.getOrgCode(employeeId);
            recording.setOrgCode(orgCode);
            recording.setUploaderEmployeeId(employeeId);
            
            // 解析核实日期
            String verifyDateStr = request.getVerifyDate();
            if (verifyDateStr != null && !verifyDateStr.isEmpty()) {
                LocalDate verifyDate = LocalDate.parse(verifyDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
                recording.setVerifyDate(verifyDate);
            }
            
            // 设置核实相关字段
            recording.setBusinessAcceptanceNo(request.getBusinessAcceptanceNo());
            recording.setVerifiedUnitName(request.getVerifiedUnitName());
            recording.setPaymentAmount(request.getPaymentAmount());
            recording.setVerifiedPersonName(request.getVerifiedPersonName());
            recording.setVerifyPhone(request.getVerifyPhone());
            recording.setVerifierEmployeeId(request.getVerifierEmployeeId());
            recording.setVerifierName(request.getVerifierName());
            recording.setVerifyReason(request.getVerifyReason());
            recording.setVerifyResult(request.getVerifyResult());
            
            Recording savedRecording = recordingService.uploadRecording(file, recording);
            return ResponseEntity.ok(savedRecording);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("文件上传失败: " + e.getMessage());
        }
    }

    // 删除录音（仅上传者可删除自己上传的录音，管理员不可删除）
    @DeleteMapping("/{id}")
    @RequireRole({Role.UPLOADER})
    public ResponseEntity<?> deleteRecording(@PathVariable Long id, HttpServletRequest request) {
        String employeeId = request.getHeader("X-Employee-Id");
        
        // 检查录音是否存在
        return recordingService.getRecordingById(id)
                .map(recording -> {
                    // 检查是否是自己上传的录音
                    if (!employeeId.equals(recording.getUploaderEmployeeId())) {
                        Map<String, String> errorResponse = new HashMap<>();
                        errorResponse.put("message", "只能删除自己上传的录音");
                        return ResponseEntity.status(403).body((Object) errorResponse);
                    }
                    
                    recordingService.deleteRecording(id);
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "录音删除成功");
                    return ResponseEntity.ok().body((Object) response);
                })
                .orElseGet(() -> {
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("message", "录音不存在");
                    return ResponseEntity.notFound().build();
                });
    }

    // 播放录音
    @GetMapping(value = {"/play/{id}", "/play/{id}/{fileName}"})
    public void playRecording(@PathVariable Long id, HttpServletResponse response) {
        recordingService.getRecordingById(id).ifPresent(recording -> {
            String filePath = recording.getFilePath();
            File file = new File(filePath);
            
            if (!file.exists()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            
            // 设置 Content-Type
            String contentType = getAudioContentType(filePath);
            if (contentType == null) {
                contentType = "audio/mpeg";
            }
            response.setContentType(contentType);
            response.setContentLengthLong(file.length());
            
            // 写入文件流
            try (FileInputStream fis = new FileInputStream(file);
                 OutputStream os = response.getOutputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.flush();
            } catch (IOException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        });
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

    // 下载录音（用户、管理员可操作）
    @GetMapping("/download/{id}")
    @RequireRole({Role.USER, Role.ADMIN})
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