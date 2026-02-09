package com.recording.manager.service.impl;

import com.recording.manager.entity.Recording;
import com.recording.manager.repository.RecordingRepository;
import com.recording.manager.service.RecordingService;
import com.recording.manager.config.AppProperties;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

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
    public Page<Recording> getRecordingsByOrgCode(String orgCode, Pageable pageable) {
        return recordingRepository.findByOrgCode(orgCode, pageable);
    }
    
    @Override
    public Page<Recording> getRecordingsByOrgCodeWithFilters(String orgCode, String title, String uploader, Pageable pageable) {
        return recordingRepository.findByOrgCodeAndFilters(orgCode, title, uploader, pageable);
    }
    
    @Override
    public Page<Recording> getRecordingsByUploaderEmployeeId(String uploaderEmployeeId, Pageable pageable) {
        return recordingRepository.findByUploaderEmployeeId(uploaderEmployeeId, pageable);
    }
    
    @Override
    public Page<Recording> getRecordingsByUploaderEmployeeIdWithFilters(String uploaderEmployeeId, String title, String uploader, Pageable pageable) {
        return recordingRepository.findByUploaderEmployeeIdAndFilters(uploaderEmployeeId, title, uploader, pageable);
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
    public Recording uploadRecording(MultipartFile file, Recording recording) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }

        // 获取当前日期和时间
        LocalDate now = LocalDate.now();
        LocalTime time = LocalTime.now();
        
        // 构建日期子目录路径: /base/2025/11/26/
        String dateSubDir = String.format("%d/%02d/%02d", 
                now.getYear(), now.getMonthValue(), now.getDayOfMonth());
        
        // 创建完整的上传目录（基础路径 + 日期子目录）
        Path uploadDir = Paths.get(appProperties.getPath(), dateSubDir);
        Files.createDirectories(uploadDir);

        // 解析原始文件名
        String originalFileName = StringUtils.hasText(file.getOriginalFilename()) 
                ? file.getOriginalFilename() 
                : "";
        String baseName = StringUtils.stripFilenameExtension(originalFileName);
        String extension = StringUtils.getFilenameExtension(originalFileName);
        
        // 默认值处理
        if (!StringUtils.hasText(baseName)) {
            baseName = "录音";
        }
        if (!StringUtils.hasText(extension)) {
            extension = "wav";
        }
        
        // 构建文件名格式: HHMMSS_原始文件名_时间戳.wav
        String timeStr = String.format("%02d%02d%02d", time.getHour(), time.getMinute(), time.getSecond());
        String timestamp = String.valueOf(System.currentTimeMillis());
        String fileName = timeStr + "_" + baseName + "_" + timestamp + "." + extension;
        
        // 完整文件路径
        String filePath = uploadDir.toString() + "/" + fileName;

        // 保存文件
        try (InputStream inputStream = file.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(filePath)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        // 设置文件相关信息
        recording.setFileName(fileName);
        recording.setFilePath(filePath);
        recording.setFileSize(file.getSize());
        
        // 获取音频时长（支持 WAV 和 MP3）
        String duration = getAudioDuration(filePath);
        recording.setDuration(duration);

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
    
    /**
     * 使用 jaudiotagger 获取音频时长（支持 WAV 和 MP3）
     */
    private String getAudioDuration(String filePath) {
        if (filePath == null) {
            return "未知";
        }
        
        try {
            AudioFile audioFile = AudioFileIO.read(new File(filePath));
            int totalSeconds = audioFile.getAudioHeader().getTrackLength();
            
            if (totalSeconds <= 0) {
                return "未知";
            }
            
            int hours = totalSeconds / 3600;
            int minutes = (totalSeconds % 3600) / 60;
            int secs = totalSeconds % 60;
            
            return hours > 0 
                    ? String.format("%02d:%02d:%02d", hours, minutes, secs)
                    : String.format("%02d:%02d", minutes, secs);
        } catch (Exception e) {
            return "未知";
        }
    }
}