/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.repository;

import cn.zhuatech.vms.model.ApprovalTask;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ApprovalTaskRepository extends JpaRepository<ApprovalTask, Long> {
    List<ApprovalTask> findAllByOrderByCreatedAtDesc();
    List<ApprovalTask> findByAppointmentNoOrderByCreatedAtAsc(String appointmentNo);
    Optional<ApprovalTask> findFirstByAppointmentNoAndStatusOrderByCreatedAtAsc(String appointmentNo, String status);
    long countByStatus(String status);
    long countByStatusAndDueAtBefore(String status, LocalDateTime dueAt);
}
