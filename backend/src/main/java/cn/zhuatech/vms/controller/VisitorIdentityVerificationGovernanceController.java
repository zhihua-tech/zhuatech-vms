/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.controller;

import cn.zhuatech.vms.common.ApiResponse;
import cn.zhuatech.vms.service.VisitorIdentityVerificationGovernanceService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
@RestController
@RequestMapping("/api/enterprise/vms")
public class VisitorIdentityVerificationGovernanceController {
    private final VisitorIdentityVerificationGovernanceService service;

    /** 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
    public VisitorIdentityVerificationGovernanceController(VisitorIdentityVerificationGovernanceService service) {
        this.service = service;
    }

    /** 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
    @PostMapping("/visitor-identity-verification")
    public ApiResponse<VisitorIdentityVerificationGovernanceService.Assessment> assess(
            @Valid @RequestBody VisitorIdentityVerificationGovernanceService.Request request) {
        return ApiResponse.ok(service.assess(request));
    }
}
