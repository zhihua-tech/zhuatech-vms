/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.repository;
import cn.zhuatech.vms.model.VisitorBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface VisitorBadgeRepository extends JpaRepository<VisitorBadge, Long> {
    List<VisitorBadge> findAllByOrderByBadgeNoAsc();
    Optional<VisitorBadge> findByBadgeNo(String badgeNo);
    Optional<VisitorBadge> findByAppointmentNo(String appointmentNo);
    long countByStatus(String status);
}
