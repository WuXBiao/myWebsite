package com.recording.manager.repository;

import com.recording.manager.entity.Recording;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecordingRepository extends JpaRepository<Recording, Long> {
    
    // 分页查询所有录音
    Page<Recording> findByTitleContainingOrDescriptionContaining(
            String titleKeyword, 
            String descriptionKeyword, 
            Pageable pageable);
    
    // 根据上传者查询
    List<Recording> findByUploader(String uploader);
    
    // 自定义查询支持多条件筛选
    @Query("SELECT r FROM Recording r WHERE " +
           "(:title IS NULL OR r.title LIKE %:title%) AND " +
           "(:uploader IS NULL OR r.uploader LIKE %:uploader%)")
    Page<Recording> findByFilters(@Param("title") String title, 
                                  @Param("uploader") String uploader, 
                                  Pageable pageable);
}