/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.service;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Service
public class VisitorAccessGovernanceService {
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public Result evaluate(Request request) {
        List<String> controls = new ArrayList<>();
        if (!request.identityVerified()) controls.add("访客身份未核验");
        if (!request.hostConfirmed()) controls.add("接待人未确认");
        if (!request.safetyTrainingValid()) controls.add("安全培训已失效或未完成");
        if (request.contractor() && !request.contractorCredentialValid()) controls.add("承包商资质无效");
        if (request.restrictedZonesRequested() > 0 && !request.escortAssigned()) controls.add("受限区域访问未安排陪同人");
        String decision = !request.identityVerified() || !request.hostConfirmed()
                || (request.contractor() && !request.contractorCredentialValid()) ? "DENY"
                : controls.isEmpty() ? "GRANT" : "ESCORT_ONLY";
        int permittedZones = "GRANT".equals(decision) ? request.restrictedZonesRequested()
                : "ESCORT_ONLY".equals(decision) && request.escortAssigned() ? request.restrictedZonesRequested() : 0;
        return new Result(request.visitId(), decision, permittedZones,
                List.copyOf(controls), "GRANT".equals(decision) || "ESCORT_ONLY".equals(decision));
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record Request(@NotBlank String visitId, boolean identityVerified,
                          boolean hostConfirmed, boolean safetyTrainingValid,
                          boolean contractor, boolean contractorCredentialValid,
                          @Min(0) int restrictedZonesRequested, boolean escortAssigned) {
        /**
         * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
         */
        public Request {
            if (visitId == null || visitId.isBlank()) throw new IllegalArgumentException("visitId is required");
            if (restrictedZonesRequested < 0) throw new IllegalArgumentException("restrictedZonesRequested must be non-negative");
        }
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record Result(String visitId, String decision, int permittedRestrictedZones,
                         List<String> controlFindings, boolean badgeIssuanceAllowed) {}
}
