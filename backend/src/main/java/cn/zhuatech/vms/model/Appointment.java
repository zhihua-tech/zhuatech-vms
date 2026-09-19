/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Entity
@Table(name = "vms_appointment", indexes = {
    @Index(name = "idx_vms_appointment_status_date", columnList = "status,visitDate"),
    @Index(name = "idx_vms_appointment_site_slot", columnList = "siteCode,visitDate,timeSlot"),
    @Index(name = "idx_vms_appointment_updated", columnList = "updatedAt")
})
public class Appointment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    private Long version;
    @Column(nullable = false, unique = true, length = 40)
    private String appointmentNo;
    @Column(nullable = false, length = 40)
    private String visitorName;
    @Column(nullable = false, length = 80)
    private String visitorCompany;
    @Column(nullable = false, length = 30)
    private String visitorPhone;
    @Column(nullable = false, length = 40)
    private String hostName;
    @Column(nullable = false, length = 120)
    private String purpose;
    @Column(nullable = false)
    private LocalDate visitDate;
    @Column(nullable = false, length = 40)
    private String timeSlot;
    @Column(nullable = false, length = 60)
    private String accessArea;
    @Column(nullable = false)
    private int visitorCount;
    @Column(nullable = false, length = 24)
    private String status;
    @Column(nullable = false, length = 16)
    private String riskLevel;
    @Column(length = 32)
    private String siteCode;
    @Column(unique = true, length = 64)
    private String clientRequestId;
    @Column(length = 24)
    private String approvalStage;
    @Column(length = 32)
    private String passCode;
    private LocalDateTime checkedInAt;
    private LocalDateTime checkedOutAt;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    protected Appointment() {}

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public Appointment(String appointmentNo, String visitorName, String visitorCompany,
                       String visitorPhone, String hostName, String purpose, LocalDate visitDate,
                       String timeSlot, String accessArea, int visitorCount, String status,
                       String riskLevel) {
        this.appointmentNo = appointmentNo;
        this.visitorName = visitorName;
        this.visitorCompany = visitorCompany;
        this.visitorPhone = visitorPhone;
        this.hostName = hostName;
        this.purpose = purpose;
        this.visitDate = visitDate;
        this.timeSlot = timeSlot;
        this.accessArea = accessArea;
        this.visitorCount = visitorCount;
        this.status = status;
        this.riskLevel = riskLevel;
        this.siteCode = "SH-HQ";
        this.approvalStage = "接待人审批";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public void applyEnterpriseContext(String siteCode, String clientRequestId) {
        this.siteCode = siteCode == null || siteCode.isBlank() ? "SH-HQ" : siteCode;
        this.clientRequestId = clientRequestId == null || clientRequestId.isBlank() ? null : clientRequestId;
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public void moveToSite(String siteCode) {
        this.siteCode = siteCode == null || siteCode.isBlank() ? "SH-HQ" : siteCode;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public void update(String visitorName, String visitorCompany, String visitorPhone, String hostName,
                       String purpose, LocalDate visitDate, String timeSlot, String accessArea, int visitorCount) {
        this.visitorName = visitorName;
        this.visitorCompany = visitorCompany;
        this.visitorPhone = visitorPhone;
        this.hostName = hostName;
        this.purpose = purpose;
        this.visitDate = visitDate;
        this.timeSlot = timeSlot;
        this.accessArea = accessArea;
        this.visitorCount = visitorCount;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public void transition(String status) { this.status = status; this.updatedAt = LocalDateTime.now(); }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public void moveApprovalStage(String approvalStage) {
        this.approvalStage = approvalStage;
        this.updatedAt = LocalDateTime.now();
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public void changeRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
        this.updatedAt = LocalDateTime.now();
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public void issuePass(String passCode) { this.passCode = passCode; this.updatedAt = LocalDateTime.now(); }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public void checkIn() { this.status = "已到访"; this.checkedInAt = LocalDateTime.now(); this.updatedAt = this.checkedInAt; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public void checkOut() { this.status = "已离场"; this.checkedOutAt = LocalDateTime.now(); this.updatedAt = this.checkedOutAt; }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public Long getId() { return id; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public Long getVersion() { return version; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getAppointmentNo() { return appointmentNo; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getVisitorName() { return visitorName; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getVisitorCompany() { return visitorCompany; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getVisitorPhone() { return visitorPhone; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getHostName() { return hostName; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getPurpose() { return purpose; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public LocalDate getVisitDate() { return visitDate; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getTimeSlot() { return timeSlot; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getAccessArea() { return accessArea; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public int getVisitorCount() { return visitorCount; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getStatus() { return status; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getRiskLevel() { return riskLevel; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getSiteCode() { return siteCode == null ? "SH-HQ" : siteCode; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getClientRequestId() { return clientRequestId; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getApprovalStage() { return approvalStage; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getPassCode() { return passCode; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public LocalDateTime getCheckedInAt() { return checkedInAt; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public LocalDateTime getCheckedOutAt() { return checkedOutAt; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public LocalDateTime getCreatedAt() { return createdAt; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
