/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.controller;

import cn.zhuatech.vms.common.ApiResponse;
import cn.zhuatech.vms.model.*;
import cn.zhuatech.vms.service.EnterpriseComplianceService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@RestController
@RequestMapping("/api")
public class EnterpriseComplianceController {
    private final EnterpriseComplianceService service;
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public EnterpriseComplianceController(EnterpriseComplianceService service) { this.service = service; }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @GetMapping("/vms/sites")
    ApiResponse<List<EnterpriseSite>> sites() { return ApiResponse.ok(service.sites()); }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/admin/vms/sites")
    ApiResponse<EnterpriseSite> createSite(@Valid @RequestBody EnterpriseComplianceService.SiteRequest request) {
        return ApiResponse.ok(service.createSite(request));
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PutMapping("/admin/vms/sites/{id}")
    ApiResponse<EnterpriseSite> updateSite(@PathVariable Long id,
        @Valid @RequestBody EnterpriseComplianceService.SiteRequest request) {
        return ApiResponse.ok(service.updateSite(id, request));
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @GetMapping("/admin/vms/contractor-credentials")
    ApiResponse<List<ContractorCredential>> credentials() { return ApiResponse.ok(service.credentials()); }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/admin/vms/contractor-credentials")
    ApiResponse<ContractorCredential> createCredential(
        @Valid @RequestBody EnterpriseComplianceService.CredentialRequest request) {
        return ApiResponse.ok(service.createCredential(request));
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PutMapping("/admin/vms/contractor-credentials/{id}")
    ApiResponse<ContractorCredential> updateCredential(@PathVariable Long id,
        @Valid @RequestBody EnterpriseComplianceService.CredentialRequest request) {
        return ApiResponse.ok(service.updateCredential(id, request));
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @GetMapping("/vms/appointments/{id}/documents")
    ApiResponse<List<AppointmentDocument>> documents(@PathVariable Long id) {
        return ApiResponse.ok(service.documents(id));
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/vms/appointments/{id}/documents")
    ApiResponse<AppointmentDocument> submitDocument(@PathVariable Long id,
        @Valid @RequestBody EnterpriseComplianceService.DocumentRequest request) {
        return ApiResponse.ok(service.submitDocument(id, request));
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/admin/vms/documents/{id}/review")
    ApiResponse<AppointmentDocument> reviewDocument(@PathVariable Long id,
        @Valid @RequestBody EnterpriseComplianceService.ReviewRequest request) {
        return ApiResponse.ok(service.reviewDocument(id, request));
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @GetMapping("/vms/appointments/{id}/compliance")
    ApiResponse<EnterpriseComplianceService.ComplianceAssessment> compliance(@PathVariable Long id) {
        return ApiResponse.ok(service.assess(id));
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @GetMapping("/admin/vms/emergency/muster")
    ApiResponse<EnterpriseComplianceService.MusterSnapshot> muster(@RequestParam String siteCode) {
        return ApiResponse.ok(service.muster(siteCode));
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @GetMapping(value = "/admin/vms/exports/appointments", produces = "text/csv;charset=UTF-8")
    ResponseEntity<byte[]> export(@RequestParam String siteCode,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        byte[] data = service.exportAppointments(siteCode, from, to).getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=zhuatech-vms-appointments.csv")
            .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8)).body(data);
    }
}
