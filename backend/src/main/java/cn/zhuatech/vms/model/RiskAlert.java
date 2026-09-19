/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
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

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    protected RiskAlert() {}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public RiskAlert(String alertNo, String type, String level, String title, String relatedNo,
                     String status, String assignee) {
        this.alertNo = alertNo; this.type = type; this.level = level; this.title = title;
        this.relatedNo = relatedNo; this.status = status; this.assignee = assignee;
        this.createdAt = LocalDateTime.now();
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public void resolve(String resolution) {
        this.status = "已处理"; this.resolution = resolution; this.handledAt = LocalDateTime.now();
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public Long getId() { return id; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getAlertNo() { return alertNo; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getType() { return type; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getLevel() { return level; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getTitle() { return title; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getRelatedNo() { return relatedNo; }
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
    public LocalDateTime getCreatedAt() { return createdAt; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public LocalDateTime getHandledAt() { return handledAt; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getResolution() { return resolution; }
}
