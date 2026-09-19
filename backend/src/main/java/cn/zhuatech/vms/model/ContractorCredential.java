/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Entity
@Table(name = "vms_contractor_credential", indexes = {
    @Index(name = "idx_vms_contractor_company", columnList = "companyName"),
    @Index(name = "idx_vms_contractor_validity", columnList = "status,validUntil")
})
public class ContractorCredential {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version private Long version;
    @Column(nullable = false, length = 100)
    private String companyName;
    @Column(nullable = false, length = 40)
    private String credentialType;
    @Column(nullable = false, unique = true, length = 60)
    private String credentialNo;
    @Column(nullable = false)
    private LocalDate validUntil;
    @Column(nullable = false)
    private boolean safetyTrainingCompleted;
    @Column(nullable = false, length = 20)
    private String status;
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    protected ContractorCredential() {}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public ContractorCredential(String companyName, String credentialType, String credentialNo,
                                LocalDate validUntil, boolean safetyTrainingCompleted, String status) {
        this.companyName = companyName; this.credentialType = credentialType;
        this.credentialNo = credentialNo; this.validUntil = validUntil;
        this.safetyTrainingCompleted = safetyTrainingCompleted; this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public void update(String companyName, String credentialType, LocalDate validUntil,
                       boolean safetyTrainingCompleted, String status) {
        this.companyName = companyName; this.credentialType = credentialType;
        this.validUntil = validUntil; this.safetyTrainingCompleted = safetyTrainingCompleted;
        this.status = status; this.updatedAt = LocalDateTime.now();
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public boolean isUsableOn(LocalDate visitDate) {
        return "有效".equals(status) && !validUntil.isBefore(visitDate) && safetyTrainingCompleted;
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
    public String getCompanyName() { return companyName; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getCredentialType() { return credentialType; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getCredentialNo() { return credentialNo; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public LocalDate getValidUntil() { return validUntil; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public boolean isSafetyTrainingCompleted() { return safetyTrainingCompleted; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getStatus() { return status; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
