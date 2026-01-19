package com.recording.manager.service;

import com.recording.manager.entity.Recording;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Optional;

public interface RecordingService {
    Page<Recording> getAllRecordings(Pageable pageable);
    Page<Recording> getRecordingsWithFilters(String title, String uploader, Pageable pageable);
    Optional<Recording> getRecordingById(Long id);
    Recording saveRecording(Recording recording);
    Recording uploadRecording(MultipartFile file, String title, String description, String uploader) throws IOException;
    void deleteRecording(Long id);
    String getRecordingFilePath(Long id);
}