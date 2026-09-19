/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.repository;
import cn.zhuatech.vms.model.ContractorCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
public interface ContractorCredentialRepository extends JpaRepository<ContractorCredential, Long> {
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    List<ContractorCredential> findAllByOrderByCompanyNameAsc();
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    Optional<ContractorCredential> findFirstByCompanyNameOrderByValidUntilDesc(String companyName);
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    boolean existsByCredentialNo(String credentialNo);
}
