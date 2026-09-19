/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.repository;
import cn.zhuatech.vms.model.RiskAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
public interface RiskAlertRepository extends JpaRepository<RiskAlert, Long> {
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    List<RiskAlert> findAllByOrderByCreatedAtDesc();
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    long countByStatus(String status);
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    boolean existsByTypeAndRelatedNoAndStatus(String type, String relatedNo, String status);
}
