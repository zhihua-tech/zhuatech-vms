/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.repository;
import cn.zhuatech.vms.model.ContractorCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface ContractorCredentialRepository extends JpaRepository<ContractorCredential, Long> {
    List<ContractorCredential> findAllByOrderByCompanyNameAsc();
    Optional<ContractorCredential> findFirstByCompanyNameOrderByValidUntilDesc(String companyName);
    boolean existsByCredentialNo(String credentialNo);
}
