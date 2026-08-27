/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.repository;
import cn.zhuatech.vms.model.RiskAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface RiskAlertRepository extends JpaRepository<RiskAlert, Long> {
    List<RiskAlert> findAllByOrderByCreatedAtDesc();
    long countByStatus(String status);
    boolean existsByTypeAndRelatedNoAndStatus(String type, String relatedNo, String status);
}
