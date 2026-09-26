/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.service;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 访客放行前核验预约、证件、人证比对、关注名单、隐私同意和限制区域资质。
 *
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Service
public class VisitorIdentityVerificationGovernanceService {
    /** 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
    public Assessment assess(Request request) {
        List<String> blockers = new ArrayList<>();
        List<String> actions = new ArrayList<>();
        if (!request.appointmentActive()) blockers.add("未找到有效且在允许时段内的访客预约");
        if (!request.identityDocumentVerified() || request.identityDocumentExpired()) blockers.add("身份证件未通过核验或已过期");
        if (request.watchlistHit()) blockers.add("访客命中禁止入内或安保关注名单");
        if (!request.hostConfirmed()) blockers.add("被访人尚未确认接待");
        if (!request.privacyConsentCaptured()) blockers.add("未取得访客必要的个人信息处理同意");
        if (request.restrictedArea() && !request.securityBriefingCompleted()) blockers.add("进入限制区域前未完成安全告知");
        if (request.restrictedArea() && !request.escortAssigned()) blockers.add("限制区域访客尚未指定全程陪同人员");
        if (request.faceMatchScore() < request.minimumFaceMatchScore()) actions.add("人证比对分数不足，转人工双证件复核");
        if (request.minorVisitor() && !request.guardianConsentRecorded()) actions.add("补充未成年访客监护人同意与联系方式");
        if (!request.badgeRulesAcknowledged()) actions.add("确认访客证佩戴、转借禁止和离场归还规则");
        if (!request.auditEvidenceAttached()) actions.add("归档证件核验结果、同意、审批和放行记录");
        Decision decision = !blockers.isEmpty() ? Decision.BLOCKED
                : !actions.isEmpty() ? Decision.MANUAL_REVIEW : Decision.ALLOW;
        String accessProfile = request.restrictedArea() ? "ESCORTED_RESTRICTED" : "STANDARD_VISITOR";
        return new Assessment(request.verificationNo(), request.appointmentNo(), decision, accessProfile,
                List.copyOf(blockers), List.copyOf(actions));
    }

    /** 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
    public record Request(@NotBlank String verificationNo, @NotBlank String appointmentNo,
                          boolean appointmentActive, boolean identityDocumentVerified,
                          boolean identityDocumentExpired, boolean watchlistHit,
                          boolean hostConfirmed, boolean privacyConsentCaptured,
                          @Min(0) @Max(100) int faceMatchScore,
                          @Min(0) @Max(100) int minimumFaceMatchScore,
                          boolean restrictedArea, boolean securityBriefingCompleted,
                          boolean escortAssigned, boolean minorVisitor,
                          boolean guardianConsentRecorded, boolean badgeRulesAcknowledged,
                          boolean auditEvidenceAttached) {}

    /** 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
    public record Assessment(String verificationNo, String appointmentNo, Decision decision,
                             String accessProfile, List<String> blockers, List<String> actions) {}

    /** 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
    public enum Decision { ALLOW, MANUAL_REVIEW, BLOCKED }
}
