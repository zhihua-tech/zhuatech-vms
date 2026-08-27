/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.controller;

import cn.zhuatech.vms.common.ApiResponse;
import cn.zhuatech.vms.model.*;
import cn.zhuatech.vms.service.EnterpriseFieldService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class EnterpriseFieldController {
    private final EnterpriseFieldService service;
    public EnterpriseFieldController(EnterpriseFieldService service) { this.service = service; }

    @GetMapping("/vms/badges")
    ApiResponse<List<VisitorBadge>> badges() { return ApiResponse.ok(service.badges()); }
    @PostMapping("/vms/appointments/{id}/badges")
    ApiResponse<VisitorBadge> issueBadge(@PathVariable Long id,
        @Valid @RequestBody EnterpriseFieldService.BadgeIssueRequest request) {
        return ApiResponse.ok(service.issueBadge(id, request));
    }
    @PostMapping("/vms/badges/{id}/actions")
    ApiResponse<VisitorBadge> badgeAction(@PathVariable Long id,
        @Valid @RequestBody EnterpriseFieldService.BadgeActionRequest request) {
        return ApiResponse.ok(service.badgeAction(id, request));
    }
    @GetMapping("/vms/access-events")
    ApiResponse<List<GateAccessEvent>> accessEvents() { return ApiResponse.ok(service.accessEvents()); }
    @PostMapping("/vms/access-events")
    ApiResponse<EnterpriseFieldService.AccessDecision> recordAccess(
        @Valid @RequestBody EnterpriseFieldService.AccessRequest request) {
        return ApiResponse.ok(service.recordAccess(request));
    }
    @GetMapping("/admin/vms/field-dashboard")
    ApiResponse<EnterpriseFieldService.FieldDashboard> dashboard() { return ApiResponse.ok(service.dashboard()); }
    @GetMapping("/admin/vms/notifications")
    ApiResponse<List<NotificationTask>> notifications() { return ApiResponse.ok(service.notifications()); }
    @PostMapping("/admin/vms/notifications/dispatch")
    ApiResponse<EnterpriseFieldService.DispatchResult> dispatch() { return ApiResponse.ok(service.dispatchNotifications()); }
    @PostMapping("/admin/vms/notifications/{id}/retry")
    ApiResponse<NotificationTask> retry(@PathVariable Long id) { return ApiResponse.ok(service.retryNotification(id)); }
    @GetMapping("/admin/vms/compliance/retention-preview")
    ApiResponse<EnterpriseFieldService.RetentionPreview> retentionPreview() {
        return ApiResponse.ok(service.retentionPreview());
    }
}
