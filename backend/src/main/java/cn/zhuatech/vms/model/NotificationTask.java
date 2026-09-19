/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Entity
@Table(name = "vms_notification_task", indexes = {
    @Index(name = "idx_vms_notice_status_retry", columnList = "status,nextRetryAt"),
    @Index(name = "idx_vms_notice_reference", columnList = "referenceNo")
})
public class NotificationTask {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version private Long version;
    @Column(nullable = false, length = 40)
    private String referenceNo;
    @Column(nullable = false, length = 30)
    private String channel;
    @Column(nullable = false, length = 80)
    private String recipient;
    @Column(nullable = false, length = 40)
    private String templateCode;
    @Column(nullable = false, length = 20)
    private String status;
    @Column(nullable = false)
    private int attempts;
    @Column(length = 200)
    private String lastError;
    private LocalDateTime nextRetryAt;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    protected NotificationTask() {}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public NotificationTask(String referenceNo, String channel, String recipient, String templateCode) {
        this.referenceNo = referenceNo; this.channel = channel; this.recipient = recipient;
        this.templateCode = templateCode; this.status = "待发送"; this.createdAt = LocalDateTime.now();
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public void sent() {
        this.status = "已发送"; this.attempts++; this.sentAt = LocalDateTime.now();
        this.lastError = null; this.nextRetryAt = null;
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public void failed(String error) {
        this.status = "失败"; this.attempts++; this.lastError = error;
        this.nextRetryAt = LocalDateTime.now().plusMinutes(Math.min(60, attempts * 5L));
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public void retryNow() { this.status = "待发送"; this.nextRetryAt = null; }
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
    public String getReferenceNo() { return referenceNo; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getChannel() { return channel; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getRecipient() { return recipient; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getTemplateCode() { return templateCode; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getStatus() { return status; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public int getAttempts() { return attempts; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getLastError() { return lastError; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public LocalDateTime getNextRetryAt() { return nextRetryAt; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public LocalDateTime getCreatedAt() { return createdAt; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public LocalDateTime getSentAt() { return sentAt; }
}
