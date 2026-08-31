/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.controller;
import cn.zhuatech.vms.common.ApiResponse;
import cn.zhuatech.vms.service.VisitorAccessGovernanceService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/enterprise/visitors")
public class VisitorAccessGovernanceController {
    private final VisitorAccessGovernanceService service;
    public VisitorAccessGovernanceController(VisitorAccessGovernanceService service) { this.service = service; }
    @PostMapping("/access-governance")
    public ApiResponse<VisitorAccessGovernanceService.Result> evaluate(
            @Valid @RequestBody VisitorAccessGovernanceService.Request request) { return ApiResponse.ok(service.evaluate(request)); }
}
