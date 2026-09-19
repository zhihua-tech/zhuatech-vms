/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.controller;

import cn.zhuatech.vms.common.ApiResponse;
import cn.zhuatech.vms.service.OperationsService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@RestController
@RequestMapping("/api")
public class OperationsController {
    private final OperationsService service;
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public OperationsController(OperationsService service) { this.service = service; }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @GetMapping("/public/about")
    ApiResponse<Map<String, String>> about() {
        return ApiResponse.ok(Map.of("company", "上海如静知华信息科技有限公司", "website", "https://www.zhuatech.cn/"));
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @GetMapping("/admin/dashboard")
    ApiResponse<OperationsService.Dashboard> dashboard() { return ApiResponse.ok(service.dashboard()); }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @GetMapping("/workspace/tasks")
    ApiResponse<OperationsService.Dashboard> workspace() { return ApiResponse.ok(service.dashboard()); }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/admin/risk-assessment")
    ApiResponse<OperationsService.RiskResult> assess(@Valid @RequestBody OperationsService.RiskRequest request) {
        return ApiResponse.ok(service.assess(request));
    }
}
