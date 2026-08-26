/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.repository;
import cn.zhuatech.vms.model.SiteResource;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface SiteResourceRepository extends JpaRepository<SiteResource, Long> {
    List<SiteResource> findAllByOrderByTypeAscNameAsc();
    boolean existsByCode(String code);
    long countByStatusIn(List<String> statuses);
}
