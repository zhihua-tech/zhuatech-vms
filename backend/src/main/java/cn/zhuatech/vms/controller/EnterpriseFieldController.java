/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.controller;

import cn.zhuatech.vms.common.ApiResponse;
import cn.zhuatech.vms.model.*;
import cn.zhuatech.vms.service.EnterpriseFieldService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@RestController
@RequestMapping("/api")
public class EnterpriseFieldController {
    private final EnterpriseFieldService service;
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public EnterpriseFieldController(EnterpriseFieldService service) { this.service = service; }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @GetMapping("/vms/badges")
    ApiResponse<List<VisitorBadge>> badges() { return ApiResponse.ok(service.badges()); }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/vms/appointments/{id}/badges")
    ApiResponse<VisitorBadge> issueBadge(@PathVariable Long id,
        @Valid @RequestBody EnterpriseFieldService.BadgeIssueRequest request) {
        return ApiResponse.ok(service.issueBadge(id, request));
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/vms/badges/{id}/actions")
    ApiResponse<VisitorBadge> badgeAction(@PathVariable Long id,
        @Valid @RequestBody EnterpriseFieldService.BadgeActionRequest request) {
        return ApiResponse.ok(service.badgeAction(id, request));
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @GetMapping("/vms/access-events")
    ApiResponse<List<GateAccessEvent>> accessEvents() { return ApiResponse.ok(service.accessEvents()); }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/vms/access-events")
    ApiResponse<EnterpriseFieldService.AccessDecision> recordAccess(
        @Valid @RequestBody EnterpriseFieldService.AccessRequest request) {
        return ApiResponse.ok(service.recordAccess(request));
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @GetMapping("/admin/vms/field-dashboard")
    ApiResponse<EnterpriseFieldService.FieldDashboard> dashboard() { return ApiResponse.ok(service.dashboard()); }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @GetMapping("/admin/vms/notifications")
    ApiResponse<List<NotificationTask>> notifications() { return ApiResponse.ok(service.notifications()); }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/admin/vms/notifications/dispatch")
    ApiResponse<EnterpriseFieldService.DispatchResult> dispatch() { return ApiResponse.ok(service.dispatchNotifications()); }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/admin/vms/notifications/{id}/retry")
    ApiResponse<NotificationTask> retry(@PathVariable Long id) { return ApiResponse.ok(service.retryNotification(id)); }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @GetMapping("/admin/vms/compliance/retention-preview")
    ApiResponse<EnterpriseFieldService.RetentionPreview> retentionPreview() {
        return ApiResponse.ok(service.retentionPreview());
    }
}
