/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.controller;

import cn.zhuatech.vms.common.ApiResponse;
import cn.zhuatech.vms.service.EvacuationAccountabilityService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vms/insights")
public class EvacuationAccountabilityController {
    private final EvacuationAccountabilityService service;

    public EvacuationAccountabilityController(EvacuationAccountabilityService service) {
        this.service = service;
    }

    @PostMapping("/evacuation-accountability")
    public ApiResponse<EvacuationAccountabilityService.Result> reconcile(
        @Valid @RequestBody EvacuationAccountabilityService.Request request) {
        return ApiResponse.ok(service.reconcile(request));
    }
}
