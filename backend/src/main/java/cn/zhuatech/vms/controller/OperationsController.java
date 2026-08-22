/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.controller;

import cn.zhuatech.vms.common.ApiResponse;
import cn.zhuatech.vms.service.OperationsService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class OperationsController {
    private final OperationsService service;
    public OperationsController(OperationsService service) { this.service = service; }

    @GetMapping("/public/about")
    ApiResponse<Map<String, String>> about() {
        return ApiResponse.ok(Map.of("company", "上海如静知华信息科技有限公司", "website", "https://www.zhuatech.cn/"));
    }

    @GetMapping("/admin/dashboard")
    ApiResponse<OperationsService.Dashboard> dashboard() { return ApiResponse.ok(service.dashboard()); }

    @GetMapping("/workspace/tasks")
    ApiResponse<OperationsService.Dashboard> workspace() { return ApiResponse.ok(service.dashboard()); }

    @PostMapping("/admin/risk-assessment")
    ApiResponse<OperationsService.RiskResult> assess(@Valid @RequestBody OperationsService.RiskRequest request) {
        return ApiResponse.ok(service.assess(request));
    }
}
