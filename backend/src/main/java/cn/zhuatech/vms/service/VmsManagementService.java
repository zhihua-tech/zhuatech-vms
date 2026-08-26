/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.service;

import cn.zhuatech.vms.model.Appointment;
import cn.zhuatech.vms.model.RiskAlert;
import cn.zhuatech.vms.model.SiteResource;
import cn.zhuatech.vms.model.VisitorProfile;
import cn.zhuatech.vms.repository.AppointmentRepository;
import cn.zhuatech.vms.repository.RiskAlertRepository;
import cn.zhuatech.vms.repository.SiteResourceRepository;
import cn.zhuatech.vms.repository.VisitorProfileRepository;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final Map<String, String> settings = new LinkedHashMap<>();

    public VmsManagementService(AppointmentRepository appointments, VisitorProfileRepository visitors,
                                SiteResourceRepository resources, RiskAlertRepository alerts) {
        this.appointments = appointments;
        this.visitors = visitors;
        this.resources = resources;
        this.alerts = alerts;
        settings.put("siteName", "上海创新园区");
        settings.put("approvalMode", "接待人审批 + 安保复核");
        settings.put("passValidity", "预约时段前后 30 分钟");
        settings.put("retentionDays", "180");
        settings.put("notificationChannel", "站内消息");
    }

    public Overview overview() {
        long total = appointments.count();
        long pending = appointments.countByStatus("待审批");
        long onSite = appointments.countByStatus("已到访");
        long departed = appointments.countByStatus("已离场");
        return new Overview(total, pending, onSite, departed, alerts.countByStatus("待处理"),
            appointments.findAllByOrderByUpdatedAtDesc().stream().limit(6).toList());
    }

    public List<Appointment> listAppointments() { return appointments.findAllByOrderByUpdatedAtDesc(); }

    @Transactional
    public Appointment createAppointment(AppointmentRequest request) {
        String no = "VMS-" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + "-"
            + ThreadLocalRandom.current().nextInt(100, 1000);
        String risk = request.accessArea().contains("受限") || request.visitorCount() >= 8 ? "关注" : "正常";
        return appointments.save(new Appointment(no, request.visitorName(), request.visitorCompany(),
            request.visitorPhone(), request.hostName(), request.purpose(), request.visitDate(),
            request.timeSlot(), request.accessArea(), request.visitorCount(), "待审批", risk));
    }

    @Transactional
    public Appointment updateAppointment(Long id, AppointmentRequest request) {
        Appointment appointment = appointment(id);
        if (!List.of("待审批", "已驳回").contains(appointment.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "当前状态不允许修改预约");
        }
        appointment.update(request.visitorName(), request.visitorCompany(), request.visitorPhone(),
            request.hostName(), request.purpose(), request.visitDate(), request.timeSlot(),
            request.accessArea(), request.visitorCount());
        return appointment;
    }

    @Transactional
    public Appointment action(Long id, ActionRequest request) {
        Appointment appointment = appointment(id);
        switch (request.action()) {
            case "APPROVE" -> {
                requireStatus(appointment, "待审批");
                appointment.transition("已审批");
                appointment.issuePass(generatePass());
            }
            case "REJECT" -> {
                requireStatus(appointment, "待审批");
                appointment.transition("已驳回");
            }
            case "CANCEL" -> {
                if (List.of("已离场", "已取消").contains(appointment.getStatus())) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "当前状态不允许取消");
                }
                appointment.transition("已取消");
            }
            case "CHECK_IN" -> {
                requireStatus(appointment, "已审批");
                if (appointment.getPassCode() == null) appointment.issuePass(generatePass());
                appointment.checkIn();
            }
            case "CHECK_OUT" -> {
                requireStatus(appointment, "已到访");
                appointment.checkOut();
            }
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "不支持的预约操作");
        }
        return appointment;
    }

    public List<VisitorProfile> listVisitors() { return visitors.findAllByOrderByLastVisitAtDesc(); }

    @Transactional
    public VisitorProfile setBlacklist(Long id, BlacklistRequest request) {
        VisitorProfile visitor = visitors.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "访客档案不存在"));
        visitor.setBlacklisted(request.blacklisted(), request.reason());
        return visitor;
    }

    public List<SiteResource> listResources() { return resources.findAllByOrderByTypeAscNameAsc(); }
    public List<RiskAlert> listAlerts() { return alerts.findAllByOrderByCreatedAtDesc(); }

    @Transactional
    public RiskAlert reportException(ExceptionRequest request) {
        String no = "ALT-" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + "-"
            + ThreadLocalRandom.current().nextInt(100, 1000);
        return alerts.save(new RiskAlert(no, request.type(), request.level(), request.title(),
            request.relatedNo(), "待处理", request.assignee()));
    }

    @Transactional
    public RiskAlert resolveAlert(Long id, ResolveRequest request) {
        RiskAlert alert = alerts.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "风险预警不存在"));
        alert.resolve(request.resolution());
        return alert;
    }

    public Map<String, String> settings() { return Map.copyOf(settings); }
    public Map<String, String> updateSettings(SettingsRequest request) {
        settings.put("siteName", request.siteName());
        settings.put("approvalMode", request.approvalMode());
        settings.put("passValidity", request.passValidity());
        settings.put("retentionDays", String.valueOf(request.retentionDays()));
        settings.put("notificationChannel", request.notificationChannel());
        return settings();
    }

    private Appointment appointment(Long id) {
        return appointments.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "预约记录不存在"));
    }
    private void requireStatus(Appointment appointment, String expected) {
        if (!expected.equals(appointment.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "预约状态应为“" + expected + "”，当前为“" + appointment.getStatus() + "”");
        }
    }
    private String generatePass() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
    }

    public record Overview(long totalAppointments, long pendingApproval, long visitorsOnSite,
                           long departed, long openAlerts, List<Appointment> recentAppointments) {}
    public record AppointmentRequest(@NotBlank @Size(max = 40) String visitorName,
        @NotBlank @Size(max = 80) String visitorCompany,
        @NotBlank @Size(max = 30) String visitorPhone,
        @NotBlank @Size(max = 40) String hostName,
        @NotBlank @Size(max = 120) String purpose,
        @NotNull LocalDate visitDate,
        @NotBlank @Size(max = 40) String timeSlot,
        @NotBlank @Size(max = 60) String accessArea,
        @Min(1) @Max(200) int visitorCount) {}
    public record ActionRequest(@NotBlank String action, @Size(max = 200) String remark) {}
    public record BlacklistRequest(boolean blacklisted, @NotBlank @Size(max = 200) String reason) {}
    public record ExceptionRequest(@NotBlank String type, @NotBlank String level,
        @NotBlank @Size(max = 120) String title, @NotBlank String relatedNo,
        @NotBlank String assignee) {}
    public record ResolveRequest(@NotBlank @Size(max = 200) String resolution) {}
    public record SettingsRequest(@NotBlank String siteName, @NotBlank String approvalMode,
        @NotBlank String passValidity, @Min(30) @Max(3650) int retentionDays,
        @NotBlank String notificationChannel) {}
}
