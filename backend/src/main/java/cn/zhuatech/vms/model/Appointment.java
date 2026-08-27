/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

    protected Appointment() {}

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

    public void applyEnterpriseContext(String siteCode, String clientRequestId) {
        this.siteCode = siteCode == null || siteCode.isBlank() ? "SH-HQ" : siteCode;
        this.clientRequestId = clientRequestId == null || clientRequestId.isBlank() ? null : clientRequestId;
    }

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

    public void transition(String status) { this.status = status; this.updatedAt = LocalDateTime.now(); }
    public void moveApprovalStage(String approvalStage) {
        this.approvalStage = approvalStage;
        this.updatedAt = LocalDateTime.now();
    }
    public void changeRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
        this.updatedAt = LocalDateTime.now();
    }
    public void issuePass(String passCode) { this.passCode = passCode; this.updatedAt = LocalDateTime.now(); }
    public void checkIn() { this.status = "已到访"; this.checkedInAt = LocalDateTime.now(); this.updatedAt = this.checkedInAt; }
    public void checkOut() { this.status = "已离场"; this.checkedOutAt = LocalDateTime.now(); this.updatedAt = this.checkedOutAt; }

    public Long getId() { return id; }
    public Long getVersion() { return version; }
    public String getAppointmentNo() { return appointmentNo; }
    public String getVisitorName() { return visitorName; }
    public String getVisitorCompany() { return visitorCompany; }
    public String getVisitorPhone() { return visitorPhone; }
    public String getHostName() { return hostName; }
    public String getPurpose() { return purpose; }
    public LocalDate getVisitDate() { return visitDate; }
    public String getTimeSlot() { return timeSlot; }
    public String getAccessArea() { return accessArea; }
    public int getVisitorCount() { return visitorCount; }
    public String getStatus() { return status; }
    public String getRiskLevel() { return riskLevel; }
    public String getSiteCode() { return siteCode == null ? "SH-HQ" : siteCode; }
    public String getClientRequestId() { return clientRequestId; }
    public String getApprovalStage() { return approvalStage; }
    public String getPassCode() { return passCode; }
    public LocalDateTime getCheckedInAt() { return checkedInAt; }
    public LocalDateTime getCheckedOutAt() { return checkedOutAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
