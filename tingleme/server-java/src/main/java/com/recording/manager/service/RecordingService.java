package com.recording.manager.service;

import com.recording.manager.entity.Recording;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Optional;

public interface RecordingService {
    
    /**
     * 分页获取所有录音列表
     * @param pageable 分页参数
     * @return 分页录音数据
     */
    Page<Recording> getAllRecordings(Pageable pageable);
    
    /**
     * 根据筛选条件分页获取录音列表
     * @param title 标题筛选条件（可为null）
     * @param uploader 上传者筛选条件（可为null）
     * @param pageable 分页参数
     * @return 符合条件的分页录音数据
     */
    Page<Recording> getRecordingsWithFilters(String title, String uploader, Pageable pageable);
    
    /**
     * 根据 ID 获取录音详情
     * @param id 录音ID
     * @return 录音对象（可能不存在）
     */
    Optional<Recording> getRecordingById(Long id);
    
    /**
     * 保存录音信息
     * @param recording 录音对象
     * @return 保存后的录音对象
     */
    Recording saveRecording(Recording recording);
    
    /**
     * 上传录音文件
     * @param file 上传的音频文件
     * @param title 录音标题
     * @param description 录音描述（可为null）
     * @param uploader 上传者（可为null）
     * @return 保存后的录音对象
     * @throws IOException 文件保存失败时抛出异常
     */
    Recording uploadRecording(MultipartFile file, String title, String description, String uploader) throws IOException;
    
    /**
     * 删除录音（同时删除物理文件和数据库记录）
     * @param id 录音ID
     */
    void deleteRecording(Long id);
    
    /**
     * 获取录音文件的存储路径
     * @param id 录音ID
     * @return 文件路径（不存在则返回null）
     */
    String getRecordingFilePath(Long id);
}