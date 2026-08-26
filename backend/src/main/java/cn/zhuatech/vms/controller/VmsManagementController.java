/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.controller;

import cn.zhuatech.vms.common.ApiResponse;
import cn.zhuatech.vms.model.AuditLog;
import cn.zhuatech.vms.model.Appointment;
import cn.zhuatech.vms.model.RiskAlert;
import cn.zhuatech.vms.model.SiteResource;
import cn.zhuatech.vms.model.VisitorProfile;
import cn.zhuatech.vms.service.VmsManagementService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class VmsManagementController {
    private final VmsManagementService service;
    public VmsManagementController(VmsManagementService service) { this.service = service; }

    @GetMapping("/vms/overview")
    ApiResponse<VmsManagementService.Overview> overview() { return ApiResponse.ok(service.overview()); }

    @GetMapping("/vms/appointments")
    ApiResponse<List<Appointment>> appointments() { return ApiResponse.ok(service.listAppointments()); }

    @PostMapping("/vms/appointments")
    ApiResponse<Appointment> create(@Valid @RequestBody VmsManagementService.AppointmentRequest request) {
        return ApiResponse.ok(service.createAppointment(request));
    }

    @PutMapping("/vms/appointments/{id}")
    ApiResponse<Appointment> update(@PathVariable Long id,
        @Valid @RequestBody VmsManagementService.AppointmentRequest request) {
        return ApiResponse.ok(service.updateAppointment(id, request));
    }

    @PostMapping("/vms/appointments/{id}/actions")
    ApiResponse<Appointment> action(@PathVariable Long id,
        @Valid @RequestBody VmsManagementService.ActionRequest request) {
        return ApiResponse.ok(service.action(id, request));
    }

    @PostMapping("/vms/passes/verify")
    ApiResponse<VmsManagementService.PassVerification> verifyPass(
        @Valid @RequestBody VmsManagementService.PassRequest request) {
        return ApiResponse.ok(service.verifyPass(request));
    }

    @GetMapping("/vms/visitors")
    ApiResponse<List<VisitorProfile>> visitors() { return ApiResponse.ok(service.listVisitors()); }

    @PostMapping("/admin/vms/visitors/{id}/blacklist")
    ApiResponse<VisitorProfile> blacklist(@PathVariable Long id,
        @Valid @RequestBody VmsManagementService.BlacklistRequest request) {
        return ApiResponse.ok(service.setBlacklist(id, request));
    }

    @PostMapping("/vms/visitors/{id}/identity")
    ApiResponse<VisitorProfile> verifyIdentity(@PathVariable Long id,
        @Valid @RequestBody VmsManagementService.IdentityRequest request) {
        return ApiResponse.ok(service.verifyIdentity(id, request));
    }

    @GetMapping("/vms/resources")
    ApiResponse<List<SiteResource>> resources() { return ApiResponse.ok(service.listResources()); }

    @PostMapping("/admin/vms/resources")
    ApiResponse<SiteResource> createResource(@Valid @RequestBody VmsManagementService.ResourceRequest request) {
        return ApiResponse.ok(service.createResource(request));
    }

    @PutMapping("/admin/vms/resources/{id}")
    ApiResponse<SiteResource> updateResource(@PathVariable Long id,
        @Valid @RequestBody VmsManagementService.ResourceRequest request) {
        return ApiResponse.ok(service.updateResource(id, request));
    }

    @DeleteMapping("/admin/vms/resources/{id}")
    ApiResponse<Void> deleteResource(@PathVariable Long id) {
        service.deleteResource(id);
        return ApiResponse.ok(null);
    }

    @GetMapping("/vms/alerts")
    ApiResponse<List<RiskAlert>> alerts() { return ApiResponse.ok(service.listAlerts()); }

    @PostMapping("/vms/alerts")
    ApiResponse<RiskAlert> report(@Valid @RequestBody VmsManagementService.ExceptionRequest request) {
        return ApiResponse.ok(service.reportException(request));
    }

    @PostMapping("/vms/alerts/{id}/resolve")
    ApiResponse<RiskAlert> resolve(@PathVariable Long id,
        @Valid @RequestBody VmsManagementService.ResolveRequest request) {
        return ApiResponse.ok(service.resolveAlert(id, request));
    }

    @GetMapping("/admin/vms/settings")
    ApiResponse<Map<String, String>> settings() { return ApiResponse.ok(service.settings()); }

    @PutMapping("/admin/vms/settings")
    ApiResponse<Map<String, String>> updateSettings(
        @Valid @RequestBody VmsManagementService.SettingsRequest request) {
        return ApiResponse.ok(service.updateSettings(request));
    }

    @GetMapping("/admin/vms/reports/operations")
    ApiResponse<VmsManagementService.OperationsReport> report() { return ApiResponse.ok(service.report()); }

    @GetMapping("/admin/vms/audit-logs")
    ApiResponse<List<AuditLog>> auditLogs() { return ApiResponse.ok(service.auditLogs()); }
}
