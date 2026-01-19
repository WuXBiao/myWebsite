package com.recording.manager.service.impl;

import com.recording.manager.entity.Recording;
import com.recording.manager.repository.RecordingRepository;
import com.recording.manager.service.RecordingService;
import com.recording.manager.config.AppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class RecordingServiceImpl implements RecordingService {

    @Autowired
    private RecordingRepository recordingRepository;

    @Autowired
    private AppProperties appProperties;

    @Override
    public Page<Recording> getAllRecordings(Pageable pageable) {
        return recordingRepository.findAll(pageable);
    }

    @Override
    public Page<Recording> getRecordingsWithFilters(String title, String uploader, Pageable pageable) {
        return recordingRepository.findByFilters(title, uploader, pageable);
    }

    @Override
    public Optional<Recording> getRecordingById(Long id) {
        return recordingRepository.findById(id);
    }

    @Override
    public Recording saveRecording(Recording recording) {
        return recordingRepository.save(recording);
    }

    @Override
    public Recording uploadRecording(MultipartFile file, String title, String description, String uploader) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }

        // 创建上传目录
        Path uploadDir = Paths.get(appProperties.getPath());
        Files.createDirectories(uploadDir);

        // 生成唯一文件名
        String originalFileName = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
        String filePath = uploadDir.toString() + "/" + uniqueFileName;

        // 保存文件
        try (InputStream inputStream = file.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(filePath)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        // 创建录音对象并保存到数据库
        Recording recording = new Recording();
        recording.setTitle(title);
        recording.setDescription(description);
        recording.setFileName(originalFileName != null ? originalFileName : uniqueFileName);
        recording.setFilePath(filePath);
        recording.setFileSize(file.getSize());
        recording.setUploader(uploader);
        
        // 计算音频时长（简单估算）
        recording.setDuration(formatFileSize(file.getSize()));

        return recordingRepository.save(recording);
    }

    @Override
    public void deleteRecording(Long id) {
        Optional<Recording> recordingOpt = recordingRepository.findById(id);
        if (recordingOpt.isPresent()) {
            Recording recording = recordingOpt.get();
            // 删除物理文件
            File file = new File(recording.getFilePath());
            if (file.exists()) {
                file.delete();
            }
            recordingRepository.deleteById(id);
        }
    }

    @Override
    public String getRecordingFilePath(Long id) {
        Optional<Recording> recordingOpt = recordingRepository.findById(id);
        return recordingOpt.map(Recording::getFilePath).orElse(null);
    }

    // 辅助方法：格式化文件大小为时间显示
    private String formatFileSize(Long size) {
        if (size == null) return "未知";
        // 这里简单根据文件大小估算时长，实际应该通过音频处理库获取准确时长
        double seconds = size / 1024.0 / 32.0; // 粗略估算
        long hours = (long) (seconds / 3600);
        long minutes = (long) ((seconds % 3600) / 60);
        long secs = (long) (seconds % 60);
        
        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, secs);
        } else {
            return String.format("%02d:%02d", minutes, secs);
        }
    }
}