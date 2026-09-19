/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Entity
@Table(name = "vms_appointment_document", uniqueConstraints = {
    @UniqueConstraint(name = "uk_vms_document_type", columnNames = {"appointmentNo", "documentType"})
}, indexes = @Index(name = "idx_vms_document_appointment", columnList = "appointmentNo,status"))
public class AppointmentDocument {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version private Long version;
    @Column(nullable = false, length = 40)
    private String appointmentNo;
    @Column(nullable = false, length = 40)
    private String documentType;
    @Column(nullable = false, length = 120)
    private String fileName;
    @Column(nullable = false, length = 64)
    private String checksum;
    @Column(nullable = false, length = 20)
    private String status;
    @Column(nullable = false)
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
    @Column(length = 40)
    private String reviewedBy;
    @Column(length = 200)
    private String reviewComment;

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    protected AppointmentDocument() {}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public AppointmentDocument(String appointmentNo, String documentType, String fileName, String checksum) {
        this.appointmentNo = appointmentNo; this.documentType = documentType;
        this.fileName = fileName; this.checksum = checksum; this.status = "待审核";
        this.submittedAt = LocalDateTime.now();
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public void review(boolean approved, String reviewedBy, String comment) {
        this.status = approved ? "已通过" : "已驳回"; this.reviewedBy = reviewedBy;
        this.reviewComment = comment; this.reviewedAt = LocalDateTime.now();
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public void resubmit(String fileName, String checksum) {
        this.fileName = fileName; this.checksum = checksum; this.status = "待审核";
        this.submittedAt = LocalDateTime.now(); this.reviewedAt = null;
        this.reviewedBy = null; this.reviewComment = null;
    }
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
    public String getDocumentType() { return documentType; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getFileName() { return fileName; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getChecksum() { return checksum; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getStatus() { return status; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public LocalDateTime getReviewedAt() { return reviewedAt; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getReviewedBy() { return reviewedBy; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getReviewComment() { return reviewComment; }
}
