/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.repository;
import cn.zhuatech.vms.model.EnterpriseSite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface EnterpriseSiteRepository extends JpaRepository<EnterpriseSite, Long> {
    List<EnterpriseSite> findAllByOrderBySiteCodeAsc();
    Optional<EnterpriseSite> findBySiteCode(String siteCode);
    boolean existsBySiteCode(String siteCode);
}
