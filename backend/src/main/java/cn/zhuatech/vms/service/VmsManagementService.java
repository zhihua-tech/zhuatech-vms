/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.service;

import cn.zhuatech.vms.model.*;
import cn.zhuatech.vms.repository.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class VmsManagementService {
    private final AppointmentRepository appointments;
    private final VisitorProfileRepository visitors;
    private final SiteResourceRepository resources;
    private final RiskAlertRepository alerts;
    private final SystemSettingRepository settingRepository;
    private final AuditLogRepository auditLogs;
    private final ApprovalTaskRepository approvalTasks;
    private final NotificationTaskRepository notificationTasks;
    private final EnterpriseComplianceService compliance;

    public VmsManagementService(AppointmentRepository appointments, VisitorProfileRepository visitors,
                                SiteResourceRepository resources, RiskAlertRepository alerts,
                                SystemSettingRepository settingRepository, AuditLogRepository auditLogs,
                                ApprovalTaskRepository approvalTasks, NotificationTaskRepository notificationTasks,
                                EnterpriseComplianceService compliance) {
        this.appointments = appointments;
        this.visitors = visitors;
        this.resources = resources;
        this.alerts = alerts;
        this.settingRepository = settingRepository;
        this.auditLogs = auditLogs;
        this.approvalTasks = approvalTasks;
        this.notificationTasks = notificationTasks;
        this.compliance = compliance;
    }

    public Overview overview() {
        return new Overview(appointments.count(), appointments.countByStatus("待审批") + appointments.countByStatus("安保复核"),
            appointments.countByStatus("已到访"), appointments.countByStatus("已离场"),
            alerts.countByStatus("待处理"), appointments.findAllByOrderByUpdatedAtDesc().stream().limit(6).toList());
    }

    public List<Appointment> listAppointments() { return appointments.findAllByOrderByUpdatedAtDesc(); }

    @Transactional
    public Appointment createAppointment(AppointmentRequest request) {
        if (request.clientRequestId() != null && !request.clientRequestId().isBlank()) {
            var existing = appointments.findByClientRequestId(request.clientRequestId());
            if (existing.isPresent()) return existing.get();
        }
        visitors.findByPhone(request.visitorPhone()).filter(VisitorProfile::isBlacklisted).ifPresent(visitor -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "该访客在黑名单中，预约已被拦截");
        });
        compliance.requireActiveSite(request.siteCode());
        enforceCapacity(request);
        String no = uniqueNo("VMS");
        String risk = request.accessArea().contains("受限") || request.visitorCount() >= 8 ? "关注" : "正常";
        Appointment appointment = new Appointment(no, request.visitorName(), request.visitorCompany(),
            request.visitorPhone(), request.hostName(), request.purpose(), request.visitDate(),
            request.timeSlot(), request.accessArea(), request.visitorCount(), "待审批", risk);
        appointment.applyEnterpriseContext(request.siteCode(), request.clientRequestId());
        appointment = appointments.save(appointment);
        createApprovalTask(appointment, "接待人审批", request.hostName());
        queueNotification(appointment.getAppointmentNo(), request.hostName(), "预约待审批");
        visitors.findByPhone(request.visitorPhone()).orElseGet(() -> visitors.save(new VisitorProfile(
            request.visitorName(), request.visitorCompany(), request.visitorPhone(), false, false, 0, null, "预约自动建档")));
        audit("预约管理", "创建预约", no, request.visitorName() + " / " + request.purpose());
        return appointment;
    }

    @Transactional
    public BatchResult createAppointments(BatchAppointmentRequest request) {
        var result = new ArrayList<Appointment>();
        for (AppointmentRequest item : request.appointments()) result.add(createAppointment(item));
        audit("预约管理", "批量创建预约", "BATCH", "共处理 " + result.size() + " 条预约（含幂等命中）");
        return new BatchResult(result.size(), result);
    }

    @Transactional
    public Appointment updateAppointment(Long id, AppointmentRequest request) {
        Appointment appointment = appointment(id);
        if (!List.of("待审批", "已驳回").contains(appointment.getStatus()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "当前状态不允许修改预约");
        visitors.findByPhone(request.visitorPhone()).filter(VisitorProfile::isBlacklisted).ifPresent(visitor -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "该访客在黑名单中，预约修改已被拦截");
        });
        compliance.requireActiveSite(request.siteCode());
        enforceCapacityForUpdate(appointment, request);
        cancelPendingApproval(appointment, "预约信息修改，原审批任务失效");
        appointment.update(request.visitorName(), request.visitorCompany(), request.visitorPhone(),
            request.hostName(), request.purpose(), request.visitDate(), request.timeSlot(),
            request.accessArea(), request.visitorCount());
        appointment.moveToSite(request.siteCode());
        appointment.changeRiskLevel(request.accessArea().contains("受限") || request.visitorCount() >= 8 ? "关注" : "正常");
        appointment.transition("待审批");
        appointment.moveApprovalStage("接待人审批");
        createApprovalTask(appointment, "接待人审批", request.hostName());
        queueNotification(appointment.getAppointmentNo(), request.hostName(), "预约重新送审");
        audit("预约管理", "修改预约", appointment.getAppointmentNo(), request.purpose());
        return appointment;
    }

    @Transactional
    public Appointment action(Long id, ActionRequest request) {
        Appointment appointment = appointment(id);
        switch (request.action()) {
            case "APPROVE" -> approve(appointment, request.remark());
            case "REJECT" -> reject(appointment, request.remark());
            case "CANCEL" -> {
                if (List.of("已离场", "已取消").contains(appointment.getStatus()))
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "当前状态不允许取消");
                appointment.transition("已取消");
                cancelPendingApproval(appointment, "预约取消");
            }
            case "CHECK_IN" -> {
                requireStatus(appointment, "已审批");
                if (appointment.getPassCode() == null) appointment.issuePass(generatePass());
                appointment.checkIn();
                visitors.findByPhone(appointment.getVisitorPhone()).ifPresent(VisitorProfile::recordVisit);
            }
            case "CHECK_OUT" -> { requireStatus(appointment, "已到访"); appointment.checkOut(); }
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "不支持的预约操作");
        }
        if ("APPROVE".equals(request.action()) && "安保复核".equals(appointment.getStatus()))
            queueNotification(appointment.getAppointmentNo(), "园区安保中心", "安保复核待办");
        else if ("APPROVE".equals(request.action()))
            queueNotification(appointment.getAppointmentNo(), appointment.getVisitorPhone(), "预约审批通过");
        else if ("REJECT".equals(request.action()))
            queueNotification(appointment.getAppointmentNo(), appointment.getVisitorPhone(), "预约审批驳回");
        else if ("CHECK_IN".equals(request.action()))
            queueNotification(appointment.getAppointmentNo(), appointment.getHostName(), "访客已到访");
        else if ("CHECK_OUT".equals(request.action()))
            queueNotification(appointment.getAppointmentNo(), appointment.getVisitorPhone(), "访客已离场");
        audit("预约管理", request.action(), appointment.getAppointmentNo(), safeRemark(request.remark()));
        return appointment;
    }

    public PassVerification verifyPass(PassRequest request) {
        Appointment appointment = appointments.findByAppointmentNo(request.credential())
            .or(() -> appointments.findByPassCode(request.credential()))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "未找到有效预约或通行码"));
        boolean valid = List.of("已审批", "已到访").contains(appointment.getStatus());
        boolean blacklisted = visitors.findByPhone(appointment.getVisitorPhone())
            .map(VisitorProfile::isBlacklisted).orElse(false);
        valid = valid && !blacklisted;
        String message = blacklisted ? "访客名单状态异常，通行凭证已冻结，请联系安保复核"
            : valid ? "通行凭证有效，请核对访客证件与预约信息" : "当前预约状态不允许通行";
        audit("通行核验", valid ? "核验通过" : "核验拒绝", appointment.getAppointmentNo(), message);
        return new PassVerification(valid, message, appointment);
    }

    public List<VisitorProfile> listVisitors() { return visitors.findAllByOrderByLastVisitAtDesc(); }

    public List<ApprovalTask> listApprovalTasks() {
        return approvalTasks == null ? List.of() : approvalTasks.findAllByOrderByCreatedAtDesc();
    }

    public ApprovalBoard approvalBoard() {
        if (approvalTasks == null) return new ApprovalBoard(0, 0, 0, 0, List.of());
        long pending = approvalTasks.countByStatus("待处理");
        long overdue = approvalTasks.countByStatusAndDueAtBefore("待处理", LocalDateTime.now());
        long security = approvalTasks.findAllByOrderByCreatedAtDesc().stream()
            .filter(item -> "安保复核".equals(item.getStage()) && "待处理".equals(item.getStatus())).count();
        long highRisk = appointments.findAllByOrderByUpdatedAtDesc().stream()
            .filter(item -> "关注".equals(item.getRiskLevel()) && List.of("待审批", "安保复核").contains(item.getStatus())).count();
        return new ApprovalBoard(pending, overdue, security, highRisk,
            approvalTasks.findAllByOrderByCreatedAtDesc().stream().limit(50).toList());
    }

    @Transactional
    public InspectionResult inspectOverstay() {
        int inspected = 0;
        int generated = 0;
        LocalDateTime now = LocalDateTime.now();
        for (Appointment appointment : appointments.findByStatus("已到访")) {
            inspected++;
            LocalDateTime expectedCheckout = expectedCheckout(appointment);
            if (expectedCheckout.isBefore(now) && !alerts.existsByTypeAndRelatedNoAndStatus(
                    "超时未离场", appointment.getAppointmentNo(), "待处理")) {
                long minutes = java.time.Duration.between(expectedCheckout, now).toMinutes();
                alerts.save(new RiskAlert(uniqueNo("ALT"), "超时未离场", minutes >= 60 ? "高" : "中",
                    appointment.getVisitorName() + " 已超预约时段 " + minutes + " 分钟",
                    appointment.getAppointmentNo(), "待处理", "园区安保中心"));
                generated++;
            }
        }
        audit("企业管控", "超时巡检", "INSPECTION", "检查 " + inspected + " 条在园记录，新增 " + generated + " 条预警");
        return new InspectionResult(inspected, generated, now);
    }

    @Transactional
    public VisitorProfile setBlacklist(Long id, BlacklistRequest request) {
        VisitorProfile visitor = visitor(id);
        visitor.setBlacklisted(request.blacklisted(), request.reason());
        audit("访客档案", request.blacklisted() ? "加入黑名单" : "解除黑名单", String.valueOf(id), request.reason());
        return visitor;
    }

    @Transactional
    public VisitorProfile verifyIdentity(Long id, IdentityRequest request) {
        VisitorProfile visitor = visitor(id);
        visitor.verifyIdentity(request.verified(), request.note());
        audit("访客档案", request.verified() ? "身份核验通过" : "身份核验撤销", String.valueOf(id), request.note());
        return visitor;
    }

    public List<SiteResource> listResources() { return resources.findAllByOrderByTypeAscNameAsc(); }

    @Transactional
    public SiteResource createResource(ResourceRequest request) {
        if (resources.existsByCode(request.code()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "资源编码已存在");
        SiteResource resource = resources.save(new SiteResource(request.type(), request.code(), request.name(),
            request.department(), request.status()));
        audit("资源中心", "新增资源", request.code(), request.name());
        return resource;
    }

    @Transactional
    public SiteResource updateResource(Long id, ResourceRequest request) {
        SiteResource resource = resource(id);
        resource.update(request.type(), request.name(), request.department(), request.status());
        audit("资源中心", "更新资源", resource.getCode(), request.name() + " / " + request.status());
        return resource;
    }

    @Transactional
    public void deleteResource(Long id) {
        SiteResource resource = resource(id);
        resources.delete(resource);
        audit("资源中心", "删除资源", resource.getCode(), resource.getName());
    }

    public List<RiskAlert> listAlerts() { return alerts.findAllByOrderByCreatedAtDesc(); }

    @Transactional
    public RiskAlert reportException(ExceptionRequest request) {
        String no = uniqueNo("ALT");
        RiskAlert alert = alerts.save(new RiskAlert(no, request.type(), request.level(), request.title(),
            request.relatedNo(), "待处理", request.assignee()));
        audit("风险预警", "上报异常", no, request.title());
        return alert;
    }

    @Transactional
    public RiskAlert resolveAlert(Long id, ResolveRequest request) {
        RiskAlert alert = alerts.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "风险预警不存在"));
        alert.resolve(request.resolution());
        audit("风险预警", "完成处置", alert.getAlertNo(), request.resolution());
        return alert;
    }

    public Map<String, String> settings() {
        var result = new LinkedHashMap<String, String>();
        settingRepository.findAll().forEach(item -> result.put(item.getKey(), item.getValue()));
        return result;
    }

    @Transactional
    public Map<String, String> updateSettings(SettingsRequest request) {
        saveSetting("siteName", request.siteName());
        saveSetting("approvalMode", request.approvalMode());
        saveSetting("passValidity", request.passValidity());
        saveSetting("retentionDays", String.valueOf(request.retentionDays()));
        saveSetting("notificationChannel", request.notificationChannel());
        if (request.slotCapacity() != null) saveSetting("slotCapacity", String.valueOf(request.slotCapacity()));
        if (request.approvalSlaHours() != null) saveSetting("approvalSlaHours", String.valueOf(request.approvalSlaHours()));
        audit("基础设置", "保存配置", "SYSTEM", request.siteName() + " / 留存 " + request.retentionDays() + " 天");
        return settings();
    }

    public OperationsReport report() {
        Map<String, Long> appointmentStatus = new LinkedHashMap<>();
        for (String status : List.of("待审批", "安保复核", "已审批", "已到访", "已离场", "已驳回", "已取消"))
            appointmentStatus.put(status, appointments.countByStatus(status));
        return new OperationsReport(appointments.count(), appointmentStatus, visitors.count(),
            visitors.countByIdentityVerifiedFalse(), visitors.countByBlacklistedTrue(), alerts.count(),
            alerts.countByStatus("待处理"), resources.count(), resources.countByStatusIn(List.of("启用", "开放", "审批开放", "在线")));
    }

    public List<AuditLog> auditLogs() { return auditLogs.findTop100ByOrderByOccurredAtDesc(); }

    private void saveSetting(String key, String value) {
        SystemSetting setting = settingRepository.findById(key).orElseGet(() -> new SystemSetting(key, value));
        setting.changeValue(value);
        settingRepository.save(setting);
    }
    private void queueNotification(String referenceNo, String recipient, String templateCode) {
        String channel = settingRepository.findById("notificationChannel")
            .map(SystemSetting::getValue).orElse("站内消息");
        notificationTasks.save(new NotificationTask(referenceNo, channel, recipient, templateCode));
    }
    private void enforceCapacity(AppointmentRequest request) {
        String siteCode = request.siteCode() == null || request.siteCode().isBlank() ? "SH-HQ" : request.siteCode();
        long occupied = appointments.activeVisitorCount(siteCode, request.visitDate(), request.timeSlot(),
            List.of("待审批", "安保复核", "已审批", "已到访"));
        int capacity = Math.min(settingInt("slotCapacity", 100), compliance.requireActiveSite(siteCode).getSlotCapacity());
        if (occupied + request.visitorCount() > capacity) throw new ResponseStatusException(HttpStatus.CONFLICT,
            "该预约时段园区容量不足：已预约 " + occupied + " 人，容量上限 " + capacity + " 人");
    }
    private void enforceCapacityForUpdate(Appointment current, AppointmentRequest request) {
        String siteCode = request.siteCode() == null || request.siteCode().isBlank() ? "SH-HQ" : request.siteCode();
        long occupied = appointments.activeVisitorCount(siteCode, request.visitDate(), request.timeSlot(),
            List.of("待审批", "安保复核", "已审批", "已到访"));
        if (current.getSiteCode().equals(siteCode) && current.getVisitDate().equals(request.visitDate())
                && current.getTimeSlot().equals(request.timeSlot()))
            occupied -= current.getVisitorCount();
        int capacity = Math.min(settingInt("slotCapacity", 100), compliance.requireActiveSite(siteCode).getSlotCapacity());
        if (occupied + request.visitorCount() > capacity) throw new ResponseStatusException(HttpStatus.CONFLICT,
            "修改后超过该时段容量上限 " + capacity + " 人");
    }
    private int settingInt(String key, int fallback) {
        try { return Integer.parseInt(settingRepository.findById(key).map(SystemSetting::getValue).orElse(String.valueOf(fallback))); }
        catch (NumberFormatException ignored) { return fallback; }
    }
    private void createApprovalTask(Appointment appointment, String stage, String assignee) {
        if (approvalTasks == null) return;
        approvalTasks.save(new ApprovalTask(appointment.getAppointmentNo(), stage, assignee,
            LocalDateTime.now().plusHours(settingInt("approvalSlaHours", 4))));
    }
    private void approve(Appointment appointment, String remark) {
        if (!List.of("待审批", "安保复核").contains(appointment.getStatus()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "当前状态不允许审批");
        ApprovalTask task = pendingApproval(appointment);
        if ("安保复核".equals(appointment.getStatus()) && !isAdmin())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "安保复核必须由管理员或安保授权账号完成");
        if ("安保复核".equals(appointment.getStatus()))
            compliance.requireReadyForSecurityApproval(appointment);
        if (task != null) task.decide("已通过", operator(), safeRemark(remark));
        if ("待审批".equals(appointment.getStatus()) && "关注".equals(appointment.getRiskLevel())) {
            appointment.transition("安保复核");
            appointment.moveApprovalStage("安保复核");
            createApprovalTask(appointment, "安保复核", "园区安保中心");
            return;
        }
        appointment.transition("已审批");
        appointment.moveApprovalStage("审批完成");
        appointment.issuePass(generatePass());
    }
    private void reject(Appointment appointment, String remark) {
        if (!List.of("待审批", "安保复核").contains(appointment.getStatus()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "当前状态不允许驳回");
        ApprovalTask task = pendingApproval(appointment);
        if ("安保复核".equals(appointment.getStatus()) && !isAdmin())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "安保复核必须由管理员或安保授权账号完成");
        if (task != null) task.decide("已驳回", operator(), safeRemark(remark));
        appointment.transition("已驳回");
        appointment.moveApprovalStage("审批终止");
    }
    private ApprovalTask pendingApproval(Appointment appointment) {
        return approvalTasks == null ? null : approvalTasks
            .findFirstByAppointmentNoAndStatusOrderByCreatedAtAsc(appointment.getAppointmentNo(), "待处理")
            .orElse(null);
    }
    private void cancelPendingApproval(Appointment appointment, String reason) {
        ApprovalTask task = pendingApproval(appointment);
        if (task != null) task.cancel(operator(), reason);
    }
    private String operator() {
        return SecurityContextHolder.getContext().getAuthentication() == null ? "system"
            : SecurityContextHolder.getContext().getAuthentication().getName();
    }
    private boolean isAdmin() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
            .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }
    private LocalDateTime expectedCheckout(Appointment appointment) {
        try {
            String end = appointment.getTimeSlot().split("-")[1].trim();
            return LocalDateTime.of(appointment.getVisitDate(), LocalTime.parse(end));
        } catch (RuntimeException ignored) {
            return LocalDateTime.of(appointment.getVisitDate(), LocalTime.of(18, 0));
        }
    }
    private Appointment appointment(Long id) { return appointments.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "预约记录不存在")); }
    private VisitorProfile visitor(Long id) { return visitors.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "访客档案不存在")); }
    private SiteResource resource(Long id) { return resources.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "园区资源不存在")); }
    private void requireStatus(Appointment appointment, String expected) {
        if (!expected.equals(appointment.getStatus())) throw new ResponseStatusException(HttpStatus.CONFLICT,
            "预约状态应为“" + expected + "”，当前为“" + appointment.getStatus() + "”");
    }
    private String uniqueNo(String prefix) { return prefix + "-" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
        + "-" + ThreadLocalRandom.current().nextInt(100000, 1000000); }
    private String generatePass() { return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000)); }
    private String safeRemark(String remark) { return remark == null || remark.isBlank() ? "未填写备注" : remark; }
    private void audit(String module, String action, String businessNo, String detail) {
        auditLogs.save(new AuditLog(module, action, businessNo, operator(), detail));
    }

    public record Overview(long totalAppointments, long pendingApproval, long visitorsOnSite,
                           long departed, long openAlerts, List<Appointment> recentAppointments) {}
    public record PassVerification(boolean valid, String message, Appointment appointment) {}
    public record BatchResult(int processed, List<Appointment> appointments) {}
    public record ApprovalBoard(long pending, long overdue, long securityReview, long highRisk,
                                List<ApprovalTask> recentTasks) {}
    public record InspectionResult(int inspected, int alertsGenerated, LocalDateTime inspectedAt) {}
    public record OperationsReport(long totalAppointments, Map<String, Long> appointmentStatus,
        long totalVisitors, long unverifiedVisitors, long blacklistedVisitors, long totalAlerts,
        long openAlerts, long totalResources, long availableResources) {}
    public record AppointmentRequest(@NotBlank @Size(max = 40) String visitorName,
        @NotBlank @Size(max = 80) String visitorCompany, @NotBlank @Size(max = 30) String visitorPhone,
        @NotBlank @Size(max = 40) String hostName, @NotBlank @Size(max = 120) String purpose,
        @NotNull LocalDate visitDate, @NotBlank @Size(max = 40) String timeSlot,
        @NotBlank @Size(max = 60) String accessArea, @Min(1) @Max(200) int visitorCount,
        @Size(max = 64) String clientRequestId, @Size(max = 32) String siteCode) {}
    public record BatchAppointmentRequest(@NotEmpty @Size(max = 50)
        List<@Valid AppointmentRequest> appointments) {}
    public record ActionRequest(@NotBlank String action, @Size(max = 200) String remark) {}
    public record PassRequest(@NotBlank @Size(max = 40) String credential) {}
    public record BlacklistRequest(boolean blacklisted, @NotBlank @Size(max = 200) String reason) {}
    public record IdentityRequest(boolean verified, @NotBlank @Size(max = 200) String note) {}
    public record ResourceRequest(@NotBlank @Size(max = 20) String type, @NotBlank @Size(max = 40) String code,
        @NotBlank @Size(max = 80) String name, @NotBlank @Size(max = 80) String department,
        @NotBlank @Size(max = 20) String status) {}
    public record ExceptionRequest(@NotBlank String type, @NotBlank String level,
        @NotBlank @Size(max = 120) String title, @NotBlank String relatedNo, @NotBlank String assignee) {}
    public record ResolveRequest(@NotBlank @Size(max = 200) String resolution) {}
    public record SettingsRequest(@NotBlank String siteName, @NotBlank String approvalMode,
        @NotBlank String passValidity, @Min(30) @Max(3650) int retentionDays,
        @NotBlank String notificationChannel, @Min(1) @Max(10000) Integer slotCapacity,
        @Min(1) @Max(72) Integer approvalSlaHours) {}
}
