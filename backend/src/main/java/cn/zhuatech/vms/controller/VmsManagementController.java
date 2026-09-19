/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.controller;

import cn.zhuatech.vms.common.ApiResponse;
import cn.zhuatech.vms.model.AuditLog;
import cn.zhuatech.vms.model.Appointment;
import cn.zhuatech.vms.model.ApprovalTask;
import cn.zhuatech.vms.model.RiskAlert;
import cn.zhuatech.vms.model.SiteResource;
import cn.zhuatech.vms.model.VisitorProfile;
import cn.zhuatech.vms.service.VmsManagementService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@RestController
@RequestMapping("/api")
public class VmsManagementController {
    private final VmsManagementService service;
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public VmsManagementController(VmsManagementService service) { this.service = service; }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @GetMapping("/vms/overview")
    ApiResponse<VmsManagementService.Overview> overview() { return ApiResponse.ok(service.overview()); }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @GetMapping("/vms/appointments")
    ApiResponse<List<Appointment>> appointments() { return ApiResponse.ok(service.listAppointments()); }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/vms/appointments")
    ApiResponse<Appointment> create(@Valid @RequestBody VmsManagementService.AppointmentRequest request) {
        return ApiResponse.ok(service.createAppointment(request));
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/vms/appointments/batch")
    ApiResponse<VmsManagementService.BatchResult> createBatch(
        @Valid @RequestBody VmsManagementService.BatchAppointmentRequest request) {
        return ApiResponse.ok(service.createAppointments(request));
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PutMapping("/vms/appointments/{id}")
    ApiResponse<Appointment> update(@PathVariable Long id,
        @Valid @RequestBody VmsManagementService.AppointmentRequest request) {
        return ApiResponse.ok(service.updateAppointment(id, request));
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/vms/appointments/{id}/actions")
    ApiResponse<Appointment> action(@PathVariable Long id,
        @Valid @RequestBody VmsManagementService.ActionRequest request) {
        return ApiResponse.ok(service.action(id, request));
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/vms/passes/verify")
    ApiResponse<VmsManagementService.PassVerification> verifyPass(
        @Valid @RequestBody VmsManagementService.PassRequest request) {
        return ApiResponse.ok(service.verifyPass(request));
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @GetMapping("/vms/approval-tasks")
    ApiResponse<List<ApprovalTask>> approvalTasks() { return ApiResponse.ok(service.listApprovalTasks()); }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @GetMapping("/admin/vms/enterprise/approval-board")
    ApiResponse<VmsManagementService.ApprovalBoard> approvalBoard() {
        return ApiResponse.ok(service.approvalBoard());
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/admin/vms/enterprise/overstay-inspections")
    ApiResponse<VmsManagementService.InspectionResult> inspectOverstay() {
        return ApiResponse.ok(service.inspectOverstay());
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @GetMapping("/vms/visitors")
    ApiResponse<List<VisitorProfile>> visitors() { return ApiResponse.ok(service.listVisitors()); }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/admin/vms/visitors/{id}/blacklist")
    ApiResponse<VisitorProfile> blacklist(@PathVariable Long id,
        @Valid @RequestBody VmsManagementService.BlacklistRequest request) {
        return ApiResponse.ok(service.setBlacklist(id, request));
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/vms/visitors/{id}/identity")
    ApiResponse<VisitorProfile> verifyIdentity(@PathVariable Long id,
        @Valid @RequestBody VmsManagementService.IdentityRequest request) {
        return ApiResponse.ok(service.verifyIdentity(id, request));
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @GetMapping("/vms/resources")
    ApiResponse<List<SiteResource>> resources() { return ApiResponse.ok(service.listResources()); }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/admin/vms/resources")
    ApiResponse<SiteResource> createResource(@Valid @RequestBody VmsManagementService.ResourceRequest request) {
        return ApiResponse.ok(service.createResource(request));
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PutMapping("/admin/vms/resources/{id}")
    ApiResponse<SiteResource> updateResource(@PathVariable Long id,
        @Valid @RequestBody VmsManagementService.ResourceRequest request) {
        return ApiResponse.ok(service.updateResource(id, request));
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @DeleteMapping("/admin/vms/resources/{id}")
    ApiResponse<Void> deleteResource(@PathVariable Long id) {
        service.deleteResource(id);
        return ApiResponse.ok(null);
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @GetMapping("/vms/alerts")
    ApiResponse<List<RiskAlert>> alerts() { return ApiResponse.ok(service.listAlerts()); }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/vms/alerts")
    ApiResponse<RiskAlert> report(@Valid @RequestBody VmsManagementService.ExceptionRequest request) {
        return ApiResponse.ok(service.reportException(request));
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/vms/alerts/{id}/resolve")
    ApiResponse<RiskAlert> resolve(@PathVariable Long id,
        @Valid @RequestBody VmsManagementService.ResolveRequest request) {
        return ApiResponse.ok(service.resolveAlert(id, request));
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @GetMapping("/admin/vms/settings")
    ApiResponse<Map<String, String>> settings() { return ApiResponse.ok(service.settings()); }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PutMapping("/admin/vms/settings")
    ApiResponse<Map<String, String>> updateSettings(
        @Valid @RequestBody VmsManagementService.SettingsRequest request) {
        return ApiResponse.ok(service.updateSettings(request));
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @GetMapping("/admin/vms/reports/operations")
    ApiResponse<VmsManagementService.OperationsReport> report() { return ApiResponse.ok(service.report()); }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @GetMapping("/admin/vms/audit-logs")
    ApiResponse<List<AuditLog>> auditLogs() { return ApiResponse.ok(service.auditLogs()); }
}
