/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.service;

import cn.zhuatech.vms.model.*;
import cn.zhuatech.vms.repository.*;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class EnterpriseComplianceService {
    private final EnterpriseSiteRepository sites;
    private final ContractorCredentialRepository credentials;
    private final AppointmentDocumentRepository documents;
    private final AppointmentRepository appointments;
    private final VisitorBadgeRepository badges;
    private final GateAccessEventRepository accessEvents;
    private final AuditLogRepository auditLogs;

    public EnterpriseComplianceService(EnterpriseSiteRepository sites,
            ContractorCredentialRepository credentials, AppointmentDocumentRepository documents,
            AppointmentRepository appointments, VisitorBadgeRepository badges,
            GateAccessEventRepository accessEvents, AuditLogRepository auditLogs) {
        this.sites = sites; this.credentials = credentials; this.documents = documents;
        this.appointments = appointments; this.badges = badges;
        this.accessEvents = accessEvents; this.auditLogs = auditLogs;
    }

    public List<EnterpriseSite> sites() { return sites.findAllByOrderBySiteCodeAsc(); }

    @Transactional
    public EnterpriseSite createSite(SiteRequest request) {
        if (sites.existsBySiteCode(request.siteCode()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "园区编码已存在");
        EnterpriseSite site = sites.save(new EnterpriseSite(request.siteCode(), request.siteName(),
            request.address(), request.timezone(), request.slotCapacity(), request.assemblyPoint(), request.status()));
        audit("园区管理", "新增园区", site.getSiteCode(), site.getSiteName());
        return site;
    }

    @Transactional
    public EnterpriseSite updateSite(Long id, SiteRequest request) {
        EnterpriseSite site = sites.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "园区不存在"));
        if (!site.getSiteCode().equals(request.siteCode()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "园区编码创建后不可修改");
        site.update(request.siteName(), request.address(), request.timezone(), request.slotCapacity(),
            request.assemblyPoint(), request.status());
        audit("园区管理", "更新园区", site.getSiteCode(), site.getStatus() + " / 容量 " + site.getSlotCapacity());
        return site;
    }

    public EnterpriseSite requireActiveSite(String siteCode) {
        String normalized = siteCode == null || siteCode.isBlank() ? "SH-HQ" : siteCode;
        EnterpriseSite site = sites.findBySiteCode(normalized)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "预约园区不存在：" + normalized));
        if (!"启用".equals(site.getStatus()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "预约园区当前已停用：" + normalized);
        return site;
    }

    public List<ContractorCredential> credentials() { return credentials.findAllByOrderByCompanyNameAsc(); }

    @Transactional
    public ContractorCredential createCredential(CredentialRequest request) {
        if (credentials.existsByCredentialNo(request.credentialNo()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "资质证书编号已存在");
        ContractorCredential credential = credentials.save(new ContractorCredential(request.companyName(),
            request.credentialType(), request.credentialNo(), request.validUntil(),
            request.safetyTrainingCompleted(), request.status()));
        audit("承包商资质", "新增资质", credential.getCredentialNo(), credential.getCompanyName());
        return credential;
    }

    @Transactional
    public ContractorCredential updateCredential(Long id, CredentialRequest request) {
        ContractorCredential credential = credentials.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "承包商资质不存在"));
        if (!credential.getCredentialNo().equals(request.credentialNo()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "证书编号创建后不可修改");
        credential.update(request.companyName(), request.credentialType(), request.validUntil(),
            request.safetyTrainingCompleted(), request.status());
        audit("承包商资质", "更新资质", credential.getCredentialNo(), credential.getStatus());
        return credential;
    }

    public List<AppointmentDocument> documents(Long appointmentId) {
        return documents.findByAppointmentNoOrderBySubmittedAtAsc(appointment(appointmentId).getAppointmentNo());
    }

    @Transactional
    public AppointmentDocument submitDocument(Long appointmentId, DocumentRequest request) {
        Appointment appointment = appointment(appointmentId);
        if (List.of("已离场", "已取消").contains(appointment.getStatus()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "已结束预约不能补充材料");
        var existing = documents.findByAppointmentNoAndDocumentType(appointment.getAppointmentNo(), request.documentType());
        if (existing.isPresent()) {
            if (!"已驳回".equals(existing.get().getStatus()))
                throw new ResponseStatusException(HttpStatus.CONFLICT, "该类型材料已提交，请等待或查看审核结果");
            existing.get().resubmit(request.fileName(), request.checksum().toLowerCase());
            audit("预约材料", "重新提交", appointment.getAppointmentNo(), request.documentType() + " / " + request.fileName());
            return existing.get();
        }
        AppointmentDocument document = documents.save(new AppointmentDocument(appointment.getAppointmentNo(),
            request.documentType(), request.fileName(), request.checksum().toLowerCase()));
        audit("预约材料", "提交材料", appointment.getAppointmentNo(), request.documentType() + " / " + request.fileName());
        return document;
    }

    @Transactional
    public AppointmentDocument reviewDocument(Long id, ReviewRequest request) {
        AppointmentDocument document = documents.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "预约材料不存在"));
        if (!"待审核".equals(document.getStatus()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "材料已经完成审核");
        document.review(request.approved(), operator(), request.comment());
        audit("预约材料", request.approved() ? "材料通过" : "材料驳回",
            document.getAppointmentNo(), document.getDocumentType() + " / " + request.comment());
        return document;
    }

    public ComplianceAssessment assess(Long appointmentId) { return assess(appointment(appointmentId)); }

    public ComplianceAssessment assess(Appointment appointment) {
        List<String> required = new ArrayList<>();
        List<String> blockers = new ArrayList<>();
        if ("关注".equals(appointment.getRiskLevel())) required.add("身份证明");
        if (appointment.getAccessArea().contains("受限")) required.add("安全承诺书");
        boolean contractorWork = isContractorWork(appointment.getPurpose());
        if (contractorWork) required.add("作业人员清单");
        List<AppointmentDocument> submitted = documents
            .findByAppointmentNoOrderBySubmittedAtAsc(appointment.getAppointmentNo());
        for (String type : required) {
            var document = submitted.stream().filter(item -> type.equals(item.getDocumentType())).findFirst();
            if (document.isEmpty()) blockers.add("缺少“" + type + "”");
            else if (!"已通过".equals(document.get().getStatus()))
                blockers.add("“" + type + "”尚未审核通过");
        }
        if (contractorWork) {
            var credential = credentials.findFirstByCompanyNameOrderByValidUntilDesc(appointment.getVisitorCompany());
            if (credential.isEmpty()) blockers.add("承包商未登记企业资质");
            else if (!credential.get().isUsableOn(appointment.getVisitDate()))
                blockers.add("承包商资质无效、已过期或安全培训未完成");
        }
        try { requireActiveSite(appointment.getSiteCode()); }
        catch (ResponseStatusException error) { blockers.add(error.getReason()); }
        return new ComplianceAssessment(blockers.isEmpty(), contractorWork, required, blockers,
            submitted, blockers.isEmpty() ? "材料与资质校验通过" : "存在 " + blockers.size() + " 项准入阻断");
    }

    public void requireReadyForSecurityApproval(Appointment appointment) {
        ComplianceAssessment result = assess(appointment);
        if (!result.ready()) throw new ResponseStatusException(HttpStatus.CONFLICT,
            "合规校验未通过：" + String.join("；", result.blockers()));
    }

    public MusterSnapshot muster(String siteCode) {
        EnterpriseSite site = requireActiveSite(siteCode);
        List<MusterPerson> people = appointments
            .findBySiteCodeAndStatusOrderByCheckedInAtAsc(site.getSiteCode(), "已到访").stream()
            .map(item -> new MusterPerson(item.getAppointmentNo(), item.getVisitorName(), item.getVisitorCompany(),
                item.getHostName(), item.getVisitorCount(), badges.findByAppointmentNo(item.getAppointmentNo())
                    .map(VisitorBadge::getBadgeNo).orElse("未发证"),
                accessEvents.findFirstByAppointmentNoOrderByOccurredAtDesc(item.getAppointmentNo())
                    .map(event -> event.getGateCode() + " / " + event.getDirection()).orElse("无门禁事件"),
                item.getCheckedInAt())).toList();
        int visitorsOnSite = people.stream().mapToInt(MusterPerson::visitorCount).sum();
        return new MusterSnapshot(site.getSiteCode(), site.getSiteName(), site.getAssemblyPoint(),
            visitorsOnSite, people.size(), LocalDateTime.now(), people);
    }

    public String exportAppointments(String siteCode, LocalDate from, LocalDate to) {
        requireActiveSite(siteCode);
        if (from.isAfter(to) || from.plusYears(1).isBefore(to))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "导出日期范围必须在一年以内");
        StringBuilder csv = new StringBuilder("\uFEFF预约编号,园区,日期,时段,访客,单位,接待人,区域,人数,状态,风险等级\n");
        for (Appointment item : appointments.findBySiteCodeAndVisitDateBetweenOrderByVisitDateAsc(siteCode, from, to)) {
            csv.append(row(item.getAppointmentNo(), item.getSiteCode(), item.getVisitDate().toString(),
                item.getTimeSlot(), item.getVisitorName(), item.getVisitorCompany(), item.getHostName(),
                item.getAccessArea(), String.valueOf(item.getVisitorCount()), item.getStatus(), item.getRiskLevel()));
        }
        audit("数据导出", "导出预约", siteCode, from + " 至 " + to);
        return csv.toString();
    }

    private Appointment appointment(Long id) { return appointments.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "预约记录不存在")); }
    private boolean isContractorWork(String purpose) {
        return purpose.contains("施工") || purpose.contains("维保") || purpose.contains("承包");
    }
    private String row(String... values) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) line.append(',');
            String value = values[i] == null ? "" : values[i];
            line.append('"').append(value.replace("\"", "\"\"")).append('"');
        }
        return line.append('\n').toString();
    }
    private String operator() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null ? "system" : authentication.getName();
    }
    private void audit(String module, String action, String businessNo, String detail) {
        auditLogs.save(new AuditLog(module, action, businessNo, operator(), detail));
    }

    public record SiteRequest(@NotBlank @Size(max = 32) String siteCode,
        @NotBlank @Size(max = 80) String siteName, @NotBlank @Size(max = 160) String address,
        @NotBlank @Size(max = 40) String timezone, @Min(1) @Max(10000) int slotCapacity,
        @NotBlank @Size(max = 80) String assemblyPoint,
        @Pattern(regexp = "启用|停用") String status) {}
    public record CredentialRequest(@NotBlank @Size(max = 100) String companyName,
        @NotBlank @Size(max = 40) String credentialType,
        @NotBlank @Size(max = 60) String credentialNo, @NotNull LocalDate validUntil,
        boolean safetyTrainingCompleted, @Pattern(regexp = "有效|冻结") String status) {}
    public record DocumentRequest(@NotBlank @Size(max = 40) String documentType,
        @NotBlank @Size(max = 120) String fileName,
        @Pattern(regexp = "[a-fA-F0-9]{64}") String checksum) {}
    public record ReviewRequest(boolean approved, @NotBlank @Size(max = 200) String comment) {}
    public record ComplianceAssessment(boolean ready, boolean contractorWork, List<String> requiredDocuments,
        List<String> blockers, List<AppointmentDocument> documents, String conclusion) {}
    public record MusterPerson(String appointmentNo, String visitorName, String visitorCompany,
        String hostName, int visitorCount, String badgeNo, String lastGate, LocalDateTime checkedInAt) {}
    public record MusterSnapshot(String siteCode, String siteName, String assemblyPoint,
        int visitorsOnSite, int appointmentCount, LocalDateTime generatedAt, List<MusterPerson> people) {}
}
