package com.recording.manager.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "recordings")
public class Recording {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column
    private String description;
    
    @Column(nullable = false)
    private String fileName;
    
    @Column(nullable = false)
    private String filePath;
    
    @Column(nullable = false)
    private Long fileSize;
    
    @Column(nullable = false)
    private String duration; // 录音时长
    
    @Column
    private String uploader;
    
    /**
     * 上传者工号
     */
    @Column(name = "uploader_employee_id")
    private String uploaderEmployeeId;
    
    @Column(name = "upload_time")
    private LocalDateTime uploadTime;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 所属机构编码
     */
    @Column(name = "org_code")
    private String orgCode;
    
    // ==================== 核实相关字段 ====================
    
    @Column(name = "verify_date")
    private LocalDate verifyDate; // 核实日期
    
    @Column(name = "business_acceptance_no")
    private String businessAcceptanceNo; // 柜面业务受理编号
    
    @Column(name = "verified_unit_name")
    private String verifiedUnitName; // 被核实单位（个人）名称
    
    @Column(name = "payment_amount", precision = 18, scale = 2)
    private BigDecimal paymentAmount; // 支付金额
    
    @Column(name = "verified_person_name")
    private String verifiedPersonName; // 录音被核实人姓名
    
    @Column(name = "verify_phone")
    private String verifyPhone; // 核实电话
    
    @Column(name = "verifier_employee_id")
    private String verifierEmployeeId; // 录音核实人工号
    
    @Column(name = "verifier_name")
    private String verifierName; // 录音核实人姓名（可能与上传人不同）
    
    @Column(name = "verify_reason")
    private String verifyReason; // 核实事由（大额核实、其他核实）
    
    @Column(name = "verify_result")
    private String verifyResult; // 核实结果（真实、不真实）
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        uploadTime = LocalDateTime.now();
    }
    
    // Getter and Setter methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getUploader() {
        return uploader;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }
    
    public String getUploaderEmployeeId() {
        return uploaderEmployeeId;
    }
    
    public void setUploaderEmployeeId(String uploaderEmployeeId) {
        this.uploaderEmployeeId = uploaderEmployeeId;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getOrgCode() {
        return orgCode;
    }
    
    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }
    
    // ==================== 核实相关字段的 Getter/Setter ====================
    
    public LocalDate getVerifyDate() {
        return verifyDate;
    }
    
    public void setVerifyDate(LocalDate verifyDate) {
        this.verifyDate = verifyDate;
    }
    
    public String getBusinessAcceptanceNo() {
        return businessAcceptanceNo;
    }
    
    public void setBusinessAcceptanceNo(String businessAcceptanceNo) {
        this.businessAcceptanceNo = businessAcceptanceNo;
    }
    
    public String getVerifiedUnitName() {
        return verifiedUnitName;
    }
    
    public void setVerifiedUnitName(String verifiedUnitName) {
        this.verifiedUnitName = verifiedUnitName;
    }
    
    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }
    
    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }
    
    public String getVerifiedPersonName() {
        return verifiedPersonName;
    }
    
    public void setVerifiedPersonName(String verifiedPersonName) {
        this.verifiedPersonName = verifiedPersonName;
    }
    
    public String getVerifyPhone() {
        return verifyPhone;
    }
    
    public void setVerifyPhone(String verifyPhone) {
        this.verifyPhone = verifyPhone;
    }
    
    public String getVerifierEmployeeId() {
        return verifierEmployeeId;
    }
    
    public void setVerifierEmployeeId(String verifierEmployeeId) {
        this.verifierEmployeeId = verifierEmployeeId;
    }
    
    public String getVerifierName() {
        return verifierName;
    }
    
    public void setVerifierName(String verifierName) {
        this.verifierName = verifierName;
    }
    
    public String getVerifyReason() {
        return verifyReason;
    }
    
    public void setVerifyReason(String verifyReason) {
        this.verifyReason = verifyReason;
    }
    
    public String getVerifyResult() {
        return verifyResult;
    }
    
    public void setVerifyResult(String verifyResult) {
        this.verifyResult = verifyResult;
    }
}