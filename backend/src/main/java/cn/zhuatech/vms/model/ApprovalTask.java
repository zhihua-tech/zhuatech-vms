/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vms_approval_task", indexes = {
    @Index(name = "idx_vms_approval_business_status", columnList = "appointmentNo,status"),
    @Index(name = "idx_vms_approval_due", columnList = "status,dueAt")
})
public class ApprovalTask {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    private Long version;
    @Column(nullable = false, length = 40)
    private String appointmentNo;
    @Column(nullable = false, length = 24)
    private String stage;
    @Column(nullable = false, length = 20)
    private String status;
    @Column(nullable = false, length = 60)
    private String assignee;
    @Column(length = 60)
    private String decisionBy;
    @Column(length = 200)
    private String comment;
    @Column(nullable = false)
    private LocalDateTime dueAt;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    private LocalDateTime decidedAt;

    protected ApprovalTask() {}

    public ApprovalTask(String appointmentNo, String stage, String assignee, LocalDateTime dueAt) {
        this.appointmentNo = appointmentNo;
        this.stage = stage;
        this.status = "待处理";
        this.assignee = assignee;
        this.dueAt = dueAt;
        this.createdAt = LocalDateTime.now();
    }

    public void decide(String status, String decisionBy, String comment) {
        this.status = status;
        this.decisionBy = decisionBy;
        this.comment = comment;
        this.decidedAt = LocalDateTime.now();
    }

    public void cancel(String decisionBy, String comment) { decide("已取消", decisionBy, comment); }
    public boolean isOverdue() { return "待处理".equals(status) && dueAt.isBefore(LocalDateTime.now()); }
    public Long getId() { return id; }
    public Long getVersion() { return version; }
    public String getAppointmentNo() { return appointmentNo; }
    public String getStage() { return stage; }
    public String getStatus() { return status; }
    public String getAssignee() { return assignee; }
    public String getDecisionBy() { return decisionBy; }
    public String getComment() { return comment; }
    public LocalDateTime getDueAt() { return dueAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getDecidedAt() { return decidedAt; }
    public boolean getOverdue() { return isOverdue(); }
}
