/* Copyright 2026 上海如静知华信息科技有限公司 */
package cn.zhuatech.vms.service;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class VisitRiskService {
    public VisitDecision assess(VisitRequest request) {
        int riskScore = Math.min(20, Math.max(0, request.visitorCount() - 5) * 2)
            + (request.afterHours() ? 20 : 0)
            + (request.restrictedArea() ? 25 : 0)
            + (request.hostConfirmed() ? 0 : 20)
            + (request.identityVerified() ? 0 : 20)
            + (request.blacklistHit() ? 100 : 0);
        riskScore = Math.min(100, riskScore);
        String decision = request.blacklistHit() || riskScore >= 70 ? "REJECT"
            : riskScore >= 30 ? "MANUAL_REVIEW" : "APPROVE";
        List<String> controls = new ArrayList<>();
        if (!request.hostConfirmed()) controls.add("联系内部接待人确认到访目的");
        if (!request.identityVerified()) controls.add("到访前完成实名与证件核验");
        if (request.afterHours()) controls.add("配置夜间通行时段并通知值班安保");
        if (request.restrictedArea()) controls.add("限定受限区域门禁权限并安排陪同");
        if (request.blacklistHit()) controls.add("停止预约并交由安保人员复核");
        if (controls.isEmpty()) controls.add("按预约时段签发一次性访客通行码");
        return new VisitDecision(riskScore, decision, controls);
    }

    public record VisitRequest(@NotNull @Min(1) @Max(200) Integer visitorCount,
        @NotNull Boolean afterHours, @NotNull Boolean restrictedArea,
        @NotNull Boolean hostConfirmed, @NotNull Boolean identityVerified,
        @NotNull Boolean blacklistHit) {}
    public record VisitDecision(int riskScore, String decision, List<String> controls) {}
}
