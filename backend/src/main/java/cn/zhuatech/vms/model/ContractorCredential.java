/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

    protected ContractorCredential() {}
    public ContractorCredential(String companyName, String credentialType, String credentialNo,
                                LocalDate validUntil, boolean safetyTrainingCompleted, String status) {
        this.companyName = companyName; this.credentialType = credentialType;
        this.credentialNo = credentialNo; this.validUntil = validUntil;
        this.safetyTrainingCompleted = safetyTrainingCompleted; this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
    public void update(String companyName, String credentialType, LocalDate validUntil,
                       boolean safetyTrainingCompleted, String status) {
        this.companyName = companyName; this.credentialType = credentialType;
        this.validUntil = validUntil; this.safetyTrainingCompleted = safetyTrainingCompleted;
        this.status = status; this.updatedAt = LocalDateTime.now();
    }
    public boolean isUsableOn(LocalDate visitDate) {
        return "有效".equals(status) && !validUntil.isBefore(visitDate) && safetyTrainingCompleted;
    }
    public Long getId() { return id; }
    public Long getVersion() { return version; }
    public String getCompanyName() { return companyName; }
    public String getCredentialType() { return credentialType; }
    public String getCredentialNo() { return credentialNo; }
    public LocalDate getValidUntil() { return validUntil; }
    public boolean isSafetyTrainingCompleted() { return safetyTrainingCompleted; }
    public String getStatus() { return status; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
