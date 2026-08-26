/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vms_risk_alert")
public class RiskAlert {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 40)
    private String alertNo;
    @Column(nullable = false, length = 30)
    private String type;
    @Column(nullable = false, length = 16)
    private String level;
    @Column(nullable = false, length = 120)
    private String title;
    @Column(nullable = false, length = 40)
    private String relatedNo;
    @Column(nullable = false, length = 20)
    private String status;
    @Column(nullable = false, length = 40)
    private String assignee;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    private LocalDateTime handledAt;
    @Column(length = 200)
    private String resolution;

    protected RiskAlert() {}
    public RiskAlert(String alertNo, String type, String level, String title, String relatedNo,
                     String status, String assignee) {
        this.alertNo = alertNo; this.type = type; this.level = level; this.title = title;
        this.relatedNo = relatedNo; this.status = status; this.assignee = assignee;
        this.createdAt = LocalDateTime.now();
    }
    public void resolve(String resolution) {
        this.status = "已处理"; this.resolution = resolution; this.handledAt = LocalDateTime.now();
    }
    public Long getId() { return id; }
    public String getAlertNo() { return alertNo; }
    public String getType() { return type; }
    public String getLevel() { return level; }
    public String getTitle() { return title; }
    public String getRelatedNo() { return relatedNo; }
    public String getStatus() { return status; }
    public String getAssignee() { return assignee; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getHandledAt() { return handledAt; }
    public String getResolution() { return resolution; }
}
