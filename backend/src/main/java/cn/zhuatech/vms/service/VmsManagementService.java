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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    public VmsManagementService(AppointmentRepository appointments, VisitorProfileRepository visitors,
                                SiteResourceRepository resources, RiskAlertRepository alerts,
                                SystemSettingRepository settingRepository, AuditLogRepository auditLogs) {
        this.appointments = appointments;
        this.visitors = visitors;
        this.resources = resources;
        this.alerts = alerts;
        this.settingRepository = settingRepository;
        this.auditLogs = auditLogs;
    }

    public Overview overview() {
        return new Overview(appointments.count(), appointments.countByStatus("待审批"),
            appointments.countByStatus("已到访"), appointments.countByStatus("已离场"),
            alerts.countByStatus("待处理"), appointments.findAllByOrderByUpdatedAtDesc().stream().limit(6).toList());
    }

    public List<Appointment> listAppointments() { return appointments.findAllByOrderByUpdatedAtDesc(); }

    @Transactional
    public Appointment createAppointment(AppointmentRequest request) {
        visitors.findByPhone(request.visitorPhone()).filter(VisitorProfile::isBlacklisted).ifPresent(visitor -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "该访客在黑名单中，预约已被拦截");
        });
        String no = uniqueNo("VMS");
        String risk = request.accessArea().contains("受限") || request.visitorCount() >= 8 ? "关注" : "正常";
        Appointment appointment = appointments.save(new Appointment(no, request.visitorName(), request.visitorCompany(),
            request.visitorPhone(), request.hostName(), request.purpose(), request.visitDate(),
            request.timeSlot(), request.accessArea(), request.visitorCount(), "待审批", risk));
        visitors.findByPhone(request.visitorPhone()).orElseGet(() -> visitors.save(new VisitorProfile(
            request.visitorName(), request.visitorCompany(), request.visitorPhone(), false, false, 0, null, "预约自动建档")));
        audit("预约管理", "创建预约", no, request.visitorName() + " / " + request.purpose());
        return appointment;
    }

    @Transactional
    public Appointment updateAppointment(Long id, AppointmentRequest request) {
        Appointment appointment = appointment(id);
        if (!List.of("待审批", "已驳回").contains(appointment.getStatus()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "当前状态不允许修改预约");
        appointment.update(request.visitorName(), request.visitorCompany(), request.visitorPhone(),
            request.hostName(), request.purpose(), request.visitDate(), request.timeSlot(),
            request.accessArea(), request.visitorCount());
        audit("预约管理", "修改预约", appointment.getAppointmentNo(), request.purpose());
        return appointment;
    }

    @Transactional
    public Appointment action(Long id, ActionRequest request) {
        Appointment appointment = appointment(id);
        switch (request.action()) {
            case "APPROVE" -> { requireStatus(appointment, "待审批"); appointment.transition("已审批"); appointment.issuePass(generatePass()); }
            case "REJECT" -> { requireStatus(appointment, "待审批"); appointment.transition("已驳回"); }
            case "CANCEL" -> {
                if (List.of("已离场", "已取消").contains(appointment.getStatus()))
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "当前状态不允许取消");
                appointment.transition("已取消");
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
        audit("预约管理", request.action(), appointment.getAppointmentNo(), safeRemark(request.remark()));
        return appointment;
    }

    public PassVerification verifyPass(PassRequest request) {
        Appointment appointment = appointments.findByAppointmentNo(request.credential())
            .or(() -> appointments.findByPassCode(request.credential()))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "未找到有效预约或通行码"));
        boolean valid = List.of("已审批", "已到访").contains(appointment.getStatus());
        String message = valid ? "通行凭证有效，请核对访客证件与预约信息" : "当前预约状态不允许通行";
        audit("通行核验", valid ? "核验通过" : "核验拒绝", appointment.getAppointmentNo(), message);
        return new PassVerification(valid, message, appointment);
    }

    public List<VisitorProfile> listVisitors() { return visitors.findAllByOrderByLastVisitAtDesc(); }

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
        audit("基础设置", "保存配置", "SYSTEM", request.siteName() + " / 留存 " + request.retentionDays() + " 天");
        return settings();
    }

    public OperationsReport report() {
        Map<String, Long> appointmentStatus = new LinkedHashMap<>();
        for (String status : List.of("待审批", "已审批", "已到访", "已离场", "已驳回", "已取消"))
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
        String operator = SecurityContextHolder.getContext().getAuthentication() == null ? "system"
            : SecurityContextHolder.getContext().getAuthentication().getName();
        auditLogs.save(new AuditLog(module, action, businessNo, operator, detail));
    }

    public record Overview(long totalAppointments, long pendingApproval, long visitorsOnSite,
                           long departed, long openAlerts, List<Appointment> recentAppointments) {}
    public record PassVerification(boolean valid, String message, Appointment appointment) {}
    public record OperationsReport(long totalAppointments, Map<String, Long> appointmentStatus,
        long totalVisitors, long unverifiedVisitors, long blacklistedVisitors, long totalAlerts,
        long openAlerts, long totalResources, long availableResources) {}
    public record AppointmentRequest(@NotBlank @Size(max = 40) String visitorName,
        @NotBlank @Size(max = 80) String visitorCompany, @NotBlank @Size(max = 30) String visitorPhone,
        @NotBlank @Size(max = 40) String hostName, @NotBlank @Size(max = 120) String purpose,
        @NotNull LocalDate visitDate, @NotBlank @Size(max = 40) String timeSlot,
        @NotBlank @Size(max = 60) String accessArea, @Min(1) @Max(200) int visitorCount) {}
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
        @NotBlank String notificationChannel) {}
}
