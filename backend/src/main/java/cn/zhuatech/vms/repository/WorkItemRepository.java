/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.repository;

import cn.zhuatech.vms.model.WorkItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
public interface WorkItemRepository extends JpaRepository<WorkItem, Long> {
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    long countByStatus(String status);
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    List<WorkItem> findTop8ByOrderByUpdatedAtDesc();
}
