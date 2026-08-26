/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.repository;
import cn.zhuatech.vms.model.VisitorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface VisitorProfileRepository extends JpaRepository<VisitorProfile, Long> {
    List<VisitorProfile> findAllByOrderByLastVisitAtDesc();
}
