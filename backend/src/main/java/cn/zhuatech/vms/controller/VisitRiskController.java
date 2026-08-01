/* Copyright 2026 上海如静知华信息科技有限公司 */
package cn.zhuatech.vms.controller;

import cn.zhuatech.vms.common.ApiResponse;
import cn.zhuatech.vms.service.VisitRiskService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/visit-risk")
public class VisitRiskController {
    private final VisitRiskService service;
    public VisitRiskController(VisitRiskService service) { this.service = service; }
    @PostMapping
    ApiResponse<VisitRiskService.VisitDecision> assess(
        @Valid @RequestBody VisitRiskService.VisitRequest request) {
        return ApiResponse.ok(service.assess(request));
    }
}
