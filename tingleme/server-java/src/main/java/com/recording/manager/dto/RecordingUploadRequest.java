package com.recording.manager.dto;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * 录音上传请求 DTO
 * 封装上传录音时的所有业务字段
 */
public class RecordingUploadRequest {
    
    @NotBlank(message = "标题不能为空")
    private String title; // 录音标题
    
    private String description; // 录音描述
    
    private String uploader; // 上传者
    
    // ==================== 核实相关字段 ====================
    
    private String verifyDate; // 核实日期（yyyy-MM-dd 格式）
    
    private String businessAcceptanceNo; // 柜面业务受理编号
    
    private String verifiedUnitName; // 被核实单位（个人）名称
    
    private BigDecimal paymentAmount; // 支付金额
    
    private String verifiedPersonName; // 录音被核实人姓名
    
    private String verifyPhone; // 核实电话
    
    private String verifierEmployeeId; // 录音核实人工号
    
    private String verifierName; // 录音核实人姓名（可能与上传人不同）
    
    private String verifyReason; // 核实事由（大额核实、其他核实）
    
    private String verifyResult; // 核实结果（真实、不真实）
    
    // ==================== Getter/Setter ====================
    
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
    
    public String getUploader() {
        return uploader;
    }
    
    public void setUploader(String uploader) {
        this.uploader = uploader;
    }
    
    public String getVerifyDate() {
        return verifyDate;
    }
    
    public void setVerifyDate(String verifyDate) {
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
