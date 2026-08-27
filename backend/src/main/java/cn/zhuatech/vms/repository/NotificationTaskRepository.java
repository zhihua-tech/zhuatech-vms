/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.repository;
import cn.zhuatech.vms.model.NotificationTask;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface NotificationTaskRepository extends JpaRepository<NotificationTask, Long> {
    List<NotificationTask> findTop100ByOrderByCreatedAtDesc();
    List<NotificationTask> findByStatusInOrderByCreatedAtAsc(List<String> statuses);
    long countByStatus(String status);
}
