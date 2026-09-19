/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.service;

import cn.zhuatech.vms.model.*;
import cn.zhuatech.vms.repository.*;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Service
public class EnterpriseFieldService {
    private final AppointmentRepository appointments;
    private final VisitorProfileRepository visitors;
    private final VisitorBadgeRepository badges;
    private final GateAccessEventRepository accessEvents;
    private final NotificationTaskRepository notifications;
    private final RiskAlertRepository alerts;
    private final AuditLogRepository auditLogs;
    private final SystemSettingRepository settings;

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public EnterpriseFieldService(AppointmentRepository appointments, VisitorProfileRepository visitors,
            VisitorBadgeRepository badges, GateAccessEventRepository accessEvents,
            NotificationTaskRepository notifications, RiskAlertRepository alerts,
            AuditLogRepository auditLogs, SystemSettingRepository settings) {
        this.appointments = appointments; this.visitors = visitors; this.badges = badges;
        this.accessEvents = accessEvents; this.notifications = notifications; this.alerts = alerts;
        this.auditLogs = auditLogs; this.settings = settings;
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public List<VisitorBadge> badges() { return badges.findAllByOrderByBadgeNoAsc(); }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public List<GateAccessEvent> accessEvents() { return accessEvents.findTop100ByOrderByOccurredAtDesc(); }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public List<NotificationTask> notifications() { return notifications.findTop100ByOrderByCreatedAtDesc(); }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Transactional
    public VisitorBadge issueBadge(Long appointmentId, BadgeIssueRequest request) {
        Appointment appointment = appointment(appointmentId);
        if (!List.of("已审批", "已到访").contains(appointment.getStatus()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "只有已审批或已到访预约可以发放访客证");
        var existing = badges.findByAppointmentNo(appointment.getAppointmentNo());
        if (existing.isPresent()) return existing.get();
        VisitorBadge badge = badges.findByBadgeNo(request.badgeNo())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "访客证编号不存在"));
        if (!"可用".equals(badge.getStatus()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "该访客证当前不可发放");
        badge.issue(appointment.getAppointmentNo(), appointment.getVisitorName());
        audit("现场通行", "发放访客证", badge.getBadgeNo(), appointment.getAppointmentNo());
        return badge;
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Transactional
    public VisitorBadge badgeAction(Long id, BadgeActionRequest request) {
        VisitorBadge badge = badges.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "访客证不存在"));
        if ("RETURN".equals(request.action())) {
            if (!"已发放".equals(badge.getStatus()))
                throw new ResponseStatusException(HttpStatus.CONFLICT, "只有已发放访客证可以归还");
            badge.returnBadge(request.remark());
            audit("现场通行", "归还访客证", badge.getBadgeNo(), request.remark());
        } else if ("REPORT_LOST".equals(request.action())) {
            if (!"已发放".equals(badge.getStatus()))
                throw new ResponseStatusException(HttpStatus.CONFLICT, "只有已发放访客证可以挂失");
            String appointmentNo = badge.getAppointmentNo();
            badge.reportLost(request.remark());
            if (!alerts.existsByTypeAndRelatedNoAndStatus("访客证挂失", appointmentNo, "待处理"))
                alerts.save(new RiskAlert(uniqueNo("ALT"), "访客证挂失", "高",
                    badge.getBadgeNo() + " 已挂失，需冻结关联门禁权限", appointmentNo, "待处理", "园区安保中心"));
            audit("现场通行", "挂失访客证", badge.getBadgeNo(), request.remark());
        } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "不支持的访客证操作");
        return badge;
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Transactional
    public AccessDecision recordAccess(AccessRequest request) {
        Appointment appointment = appointments.findByAppointmentNo(request.appointmentNo())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "预约记录不存在"));
        boolean blacklisted = visitors.findByPhone(appointment.getVisitorPhone())
            .map(VisitorProfile::isBlacklisted).orElse(false);
        String result = "拒绝";
        String reason;
        if (blacklisted) reason = "访客已进入黑名单，凭证实时冻结";
        else if ("IN".equals(request.direction()) && "已审批".equals(appointment.getStatus())) {
            appointment.checkIn();
            visitors.findByPhone(appointment.getVisitorPhone()).ifPresent(VisitorProfile::recordVisit);
            result = "允许"; reason = "入场核验通过，预约状态已更新为已到访";
        } else if ("IN".equals(request.direction()) && "已到访".equals(appointment.getStatus())) {
            reason = "防重复入场：访客当前已在园区内";
        } else if ("OUT".equals(request.direction()) && "已到访".equals(appointment.getStatus())) {
            appointment.checkOut();
            badges.findByAppointmentNo(appointment.getAppointmentNo())
                .filter(badge -> "已发放".equals(badge.getStatus()))
                .ifPresent(badge -> badge.returnBadge("离场闸机自动回收"));
            result = "允许"; reason = "离场核验通过，通行权限和访客证已回收";
        } else if ("OUT".equals(request.direction()) && "已离场".equals(appointment.getStatus())) {
            reason = "防重复离场：该预约已经完成离场";
        } else reason = "预约状态“" + appointment.getStatus() + "”不允许本次通行";
        GateAccessEvent event = accessEvents.save(new GateAccessEvent(uniqueNo("EVT"),
            appointment.getAppointmentNo(), request.gateCode(), request.direction(), result, reason, operator()));
        audit("门禁事件", request.direction(), appointment.getAppointmentNo(), result + " / " + reason);
        return new AccessDecision("允许".equals(result), reason, appointment, event);
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Transactional
    public DispatchResult dispatchNotifications() {
        int processed = 0, sent = 0, failed = 0;
        LocalDateTime now = LocalDateTime.now();
        for (NotificationTask task : notifications.findByStatusInOrderByCreatedAtAsc(List.of("待发送", "失败"))) {
            if (task.getNextRetryAt() != null && task.getNextRetryAt().isAfter(now)) continue;
            processed++;
            if ("站内消息".equals(task.getChannel())) { task.sent(); sent++; }
            else { task.failed("外部消息网关尚未配置，请接入企业微信或短信适配器"); failed++; }
        }
        audit("消息中心", "执行通知派发", "OUTBOX", "处理 " + processed + "，成功 " + sent + "，失败 " + failed);
        return new DispatchResult(processed, sent, failed);
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Transactional
    public NotificationTask retryNotification(Long id) {
        NotificationTask task = notifications.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "通知任务不存在"));
        if ("已发送".equals(task.getStatus()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "已发送通知无需重试");
        task.retryNow();
        audit("消息中心", "人工重试", task.getReferenceNo(), "任务 " + id);
        return task;
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public FieldDashboard dashboard() {
        return new FieldDashboard(badges.countByStatus("可用"), badges.countByStatus("已发放"),
            badges.countByStatus("挂失"), accessEvents.countByResult("拒绝"),
            notifications.countByStatus("待发送"), notifications.countByStatus("失败"));
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public RetentionPreview retentionPreview() {
        int retentionDays = settingInt("retentionDays", 180);
        LocalDateTime threshold = LocalDateTime.now().minusDays(retentionDays);
        return new RetentionPreview(retentionDays, threshold,
            visitors.countByLastVisitAtBefore(threshold), auditLogs.countByOccurredAtBefore(threshold),
            "仅生成预检结果，不自动删除或匿名化任何数据");
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private Appointment appointment(Long id) { return appointments.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "预约记录不存在")); }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private int settingInt(String key, int fallback) {
        try { return Integer.parseInt(settings.findById(key).map(SystemSetting::getValue).orElse(String.valueOf(fallback))); }
        catch (NumberFormatException ignored) { return fallback; }
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private String operator() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null ? "system" : authentication.getName();
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private String uniqueNo(String prefix) { return prefix + "-" + LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "-" + ThreadLocalRandom.current().nextInt(100, 1000); }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private void audit(String module, String action, String businessNo, String detail) {
        auditLogs.save(new AuditLog(module, action, businessNo, operator(), detail));
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record BadgeIssueRequest(@NotBlank @Size(max = 32) String badgeNo) {}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record BadgeActionRequest(@NotBlank String action, @NotBlank @Size(max = 200) String remark) {}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record AccessRequest(@NotBlank @Size(max = 40) String appointmentNo,
        @NotBlank @Size(max = 40) String gateCode, @Pattern(regexp = "IN|OUT") String direction) {}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record AccessDecision(boolean allowed, String message, Appointment appointment, GateAccessEvent event) {}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record DispatchResult(int processed, int sent, int failed) {}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record FieldDashboard(long availableBadges, long issuedBadges, long lostBadges,
        long deniedAccessEvents, long pendingNotifications, long failedNotifications) {}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record RetentionPreview(int retentionDays, LocalDateTime threshold, long visitorProfiles,
        long auditLogs, String action) {}
}
