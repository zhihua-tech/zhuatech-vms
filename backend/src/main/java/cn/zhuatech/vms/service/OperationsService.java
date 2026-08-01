/* Copyright 2026 上海如静知华信息科技有限公司 */
package cn.zhuatech.vms.service;

import cn.zhuatech.vms.domain.DomainCatalog;
import cn.zhuatech.vms.model.WorkItem;
import cn.zhuatech.vms.repository.WorkItemRepository;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class OperationsService {
    private final WorkItemRepository repository;
    private final DomainCatalog catalog;
    public OperationsService(WorkItemRepository repository, DomainCatalog catalog) {
        this.repository = repository; this.catalog = catalog;
    }

    public Dashboard dashboard() {
        long total = repository.count();
        long completed = repository.countByStatus("已完成");
        return new Dashboard(catalog.systemName(), catalog.sceneName(), total,
            repository.countByStatus("处理中"), repository.countByStatus("待处理"), completed,
            total == 0 ? 0 : Math.round(completed * 1000.0 / total) / 10.0,
            repository.findTop8ByOrderByUpdatedAtDesc());
    }

    public RiskResult assess(RiskRequest request) {
        int score = Math.min(30, request.delayedItems() * 6)
            + Math.min(30, request.criticalItems() * 10)
            + Math.max(0, request.capacityUtilization() - 85)
            + Math.max(0, 90 - request.dataCompleteness());
        score = Math.min(100, score);
        String level = score >= 70 ? "严重" : score >= 45 ? "高" : score >= 20 ? "关注" : "稳定";
        return new RiskResult(score, level, catalog.recommendedActions(),
            Map.of("积压事项", request.backlog(), "延期事项", request.delayedItems(), "关键事项", request.criticalItems()));
    }

    public record Dashboard(String systemName, String sceneName, long total, long processing,
        long pending, long completed, double completionRate, List<WorkItem> recentItems) {}
    public record RiskRequest(@NotNull @Min(0) @Max(10000) Integer backlog,
        @NotNull @Min(0) @Max(1000) Integer delayedItems,
        @NotNull @Min(0) @Max(1000) Integer criticalItems,
        @NotNull @Min(0) @Max(100) Integer capacityUtilization,
        @NotNull @Min(0) @Max(100) Integer dataCompleteness) {}
    public record RiskResult(int score, String level, List<String> actions, Map<String, Integer> evidence) {}
}
