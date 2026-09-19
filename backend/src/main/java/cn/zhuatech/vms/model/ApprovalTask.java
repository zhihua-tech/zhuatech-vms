/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
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

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    protected ApprovalTask() {}

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public ApprovalTask(String appointmentNo, String stage, String assignee, LocalDateTime dueAt) {
        this.appointmentNo = appointmentNo;
        this.stage = stage;
        this.status = "待处理";
        this.assignee = assignee;
        this.dueAt = dueAt;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public void decide(String status, String decisionBy, String comment) {
        this.status = status;
        this.decisionBy = decisionBy;
        this.comment = comment;
        this.decidedAt = LocalDateTime.now();
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public void cancel(String decisionBy, String comment) { decide("已取消", decisionBy, comment); }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public boolean isOverdue() { return "待处理".equals(status) && dueAt.isBefore(LocalDateTime.now()); }
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
    public String getStage() { return stage; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getStatus() { return status; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getAssignee() { return assignee; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getDecisionBy() { return decisionBy; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getComment() { return comment; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public LocalDateTime getDueAt() { return dueAt; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public LocalDateTime getCreatedAt() { return createdAt; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public LocalDateTime getDecidedAt() { return decidedAt; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public boolean getOverdue() { return isOverdue(); }
}
