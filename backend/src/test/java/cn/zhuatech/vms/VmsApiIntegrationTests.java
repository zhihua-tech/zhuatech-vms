/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class VmsApiIntegrationTests {
    @Autowired MockMvc mvc;

    @Test void publicAboutIsAccessible() throws Exception {
        mvc.perform(get("/api/public/about")).andExpect(status().isOk())
            .andExpect(jsonPath("$.data.company").value("上海如静知华信息科技有限公司"));
    }

    @Test void adminCanReadDashboardAndAssessRisk() throws Exception {
        mvc.perform(get("/api/admin/dashboard").with(httpBasic("admin", "admin123"))).andExpect(status().isOk())
            .andExpect(jsonPath("$.data.total").value(4));
        mvc.perform(post("/api/admin/risk-assessment").with(httpBasic("admin", "admin123")).contentType(MediaType.APPLICATION_JSON)
            .content("{\"backlog\":18,\"delayedItems\":3,\"criticalItems\":1,\"capacityUtilization\":91,\"dataCompleteness\":86}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.level").exists());
    }

    @Test void operatorCanUseWorkspaceButNotAdmin() throws Exception {
        mvc.perform(get("/api/workspace/tasks").with(httpBasic("operator", "operator123"))).andExpect(status().isOk());
        mvc.perform(get("/api/admin/dashboard").with(httpBasic("operator", "operator123"))).andExpect(status().isForbidden());
    }

    @Test void anonymousRequestIsRejected() throws Exception {
        mvc.perform(get("/api/workspace/tasks")).andExpect(status().isUnauthorized());
    }

    @Test void adminCanAssessVisitRisk() throws Exception {
        mvc.perform(post("/api/admin/visit-risk").with(httpBasic("admin", "admin123"))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"visitorCount\":8,\"afterHours\":true,\"restrictedArea\":true,\"hostConfirmed\":true,\"identityVerified\":false,\"blacklistHit\":false}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.riskScore").value(71))
            .andExpect(jsonPath("$.data.decision").value("REJECT"));
    }

    @Test void appointmentCanCompleteApprovalCheckInAndCheckOutWorkflow() throws Exception {
        var created = mvc.perform(post("/api/vms/appointments").with(httpBasic("operator", "operator123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"visitorName":"测试访客","visitorCompany":"知华测试伙伴","visitorPhone":"13812345678",
                    "hostName":"测试接待人","purpose":"系统验收测试","visitDate":"2026-08-27",
                    "timeSlot":"09:00-11:00","accessArea":"A座会议中心","visitorCount":2}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.status").value("待审批"))
            .andReturn();
        var idMatcher = java.util.regex.Pattern.compile("\\\"id\\\":(\\d+)")
            .matcher(created.getResponse().getContentAsString());
        org.junit.jupiter.api.Assertions.assertTrue(idMatcher.find());
        long id = Long.parseLong(idMatcher.group(1));

        mvc.perform(post("/api/vms/appointments/{id}/actions", id).with(httpBasic("operator", "operator123"))
                .contentType(MediaType.APPLICATION_JSON).content("{\"action\":\"APPROVE\",\"remark\":\"测试审批\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("已审批"))
            .andExpect(jsonPath("$.data.passCode").isNotEmpty());
        mvc.perform(post("/api/vms/appointments/{id}/actions", id).with(httpBasic("operator", "operator123"))
                .contentType(MediaType.APPLICATION_JSON).content("{\"action\":\"CHECK_IN\",\"remark\":\"测试签到\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("已到访"));
        mvc.perform(post("/api/vms/appointments/{id}/actions", id).with(httpBasic("operator", "operator123"))
                .contentType(MediaType.APPLICATION_JSON).content("{\"action\":\"CHECK_OUT\",\"remark\":\"测试签退\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("已离场"));
    }

    @Test void resourceVisitorAlertAndSettingsModulesAreAccessible() throws Exception {
        mvc.perform(get("/api/vms/overview").with(httpBasic("operator", "operator123")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.totalAppointments").isNumber());
        mvc.perform(get("/api/vms/visitors").with(httpBasic("operator", "operator123")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data").isArray());
        mvc.perform(get("/api/vms/resources").with(httpBasic("operator", "operator123")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data").isArray());
        mvc.perform(get("/api/vms/alerts").with(httpBasic("operator", "operator123")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.length()").value(2));
        mvc.perform(get("/api/admin/vms/settings").with(httpBasic("admin", "admin123")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.siteName").isNotEmpty());
        mvc.perform(get("/api/admin/vms/settings").with(httpBasic("operator", "operator123")))
            .andExpect(status().isForbidden());
    }

    @Test void invalidAppointmentTransitionReturnsConflict() throws Exception {
        mvc.perform(post("/api/vms/appointments/1/actions").with(httpBasic("operator", "operator123"))
                .contentType(MediaType.APPLICATION_JSON).content("{\"action\":\"CHECK_IN\",\"remark\":\"跳过审批\"}"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.success").value(false));
    }

    @Test void receptionCanVerifyPassAndRejectBlacklistedVisitor() throws Exception {
        mvc.perform(post("/api/vms/passes/verify").with(httpBasic("operator", "operator123"))
                .contentType(MediaType.APPLICATION_JSON).content("{\"credential\":\"VMS-20260826-102\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.valid").value(true))
            .andExpect(jsonPath("$.data.appointment.visitorName").value("林悦"));

        mvc.perform(post("/api/vms/appointments").with(httpBasic("operator", "operator123"))
                .contentType(MediaType.APPLICATION_JSON).content("""
                    {"visitorName":"高峰","visitorCompany":"个人","visitorPhone":"13500001999",
                    "hostName":"王诚","purpose":"黑名单拦截测试","visitDate":"2026-08-28",
                    "timeSlot":"09:00-11:00","accessArea":"A座会议中心","visitorCount":1}
                    """))
            .andExpect(status().isConflict()).andExpect(jsonPath("$.success").value(false));
    }

    @Test void adminCanMaintainResourcesAndReadAuditReport() throws Exception {
        var created = mvc.perform(post("/api/admin/vms/resources").with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"type\":\"门禁点\",\"code\":\"GATE-T01\",\"name\":\"测试访客通道\",\"department\":\"安保中心\",\"status\":\"在线\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.code").value("GATE-T01")).andReturn();
        var matcher = java.util.regex.Pattern.compile("\\\"id\\\":(\\d+)")
            .matcher(created.getResponse().getContentAsString());
        org.junit.jupiter.api.Assertions.assertTrue(matcher.find());
        long id = Long.parseLong(matcher.group(1));

        mvc.perform(put("/api/admin/vms/resources/{id}", id).with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"type\":\"门禁点\",\"code\":\"GATE-T01\",\"name\":\"测试访客通道\",\"department\":\"安保中心\",\"status\":\"离线\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("离线"));
        mvc.perform(get("/api/admin/vms/reports/operations").with(httpBasic("admin", "admin123")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.totalResources").isNumber());
        mvc.perform(get("/api/admin/vms/audit-logs").with(httpBasic("admin", "admin123")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.length()").isNotEmpty());
        mvc.perform(delete("/api/admin/vms/resources/{id}", id).with(httpBasic("admin", "admin123")))
            .andExpect(status().isOk());
    }

    @Test void identityAndSettingsChangesArePersisted() throws Exception {
        mvc.perform(post("/api/vms/visitors/4/identity").with(httpBasic("operator", "operator123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"verified\":true,\"note\":\"前台核验证件原件\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.identityVerified").value(true));
        mvc.perform(put("/api/admin/vms/settings").with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON).content("""
                    {"siteName":"知华科技访客中心","approvalMode":"仅接待人审批",
                    "passValidity":"仅预约时段内","retentionDays":365,"notificationChannel":"站内消息"}
                    """))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.siteName").value("知华科技访客中心"));
        mvc.perform(get("/api/admin/vms/settings").with(httpBasic("admin", "admin123")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.retentionDays").value("365"));
    }

    @Test void clientRequestIdMakesAppointmentCreationIdempotent() throws Exception {
        String payload = """
            {"visitorName":"幂等测试访客","visitorCompany":"知华测试伙伴","visitorPhone":"13812340001",
            "hostName":"周敏","purpose":"幂等预约测试","visitDate":"2026-09-03",
            "timeSlot":"09:00-11:00","accessArea":"A座会议中心","visitorCount":2,
            "clientRequestId":"TEST-IDEMPOTENCY-001","siteCode":"SH-HQ"}
            """;
        var first = mvc.perform(post("/api/vms/appointments").with(httpBasic("operator", "operator123"))
                .contentType(MediaType.APPLICATION_JSON).content(payload))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.clientRequestId").value("TEST-IDEMPOTENCY-001"))
            .andReturn().getResponse().getContentAsString();
        var second = mvc.perform(post("/api/vms/appointments").with(httpBasic("operator", "operator123"))
                .contentType(MediaType.APPLICATION_JSON).content(payload))
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        var pattern = java.util.regex.Pattern.compile("\\\"id\\\":(\\d+)");
        var firstId = pattern.matcher(first); var secondId = pattern.matcher(second);
        org.junit.jupiter.api.Assertions.assertTrue(firstId.find());
        org.junit.jupiter.api.Assertions.assertTrue(secondId.find());
        org.junit.jupiter.api.Assertions.assertEquals(firstId.group(1), secondId.group(1));
    }

    @Test void restrictedAreaRequiresHostAndSecurityApproval() throws Exception {
        var created = mvc.perform(post("/api/vms/appointments").with(httpBasic("operator", "operator123"))
                .contentType(MediaType.APPLICATION_JSON).content("""
                    {"visitorName":"受限区访客","visitorCompany":"维保服务商","visitorPhone":"13812340002",
                    "hostName":"周敏","purpose":"机房设备巡检","visitDate":"2026-09-04",
                    "timeSlot":"14:00-16:00","accessArea":"受限区-数据中心","visitorCount":2,
                    "clientRequestId":"TEST-RESTRICTED-001","siteCode":"SH-HQ"}
                    """))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.riskLevel").value("关注")).andReturn();
        var matcher = java.util.regex.Pattern.compile("\\\"id\\\":(\\d+)")
            .matcher(created.getResponse().getContentAsString());
        org.junit.jupiter.api.Assertions.assertTrue(matcher.find());
        long id = Long.parseLong(matcher.group(1));

        mvc.perform(post("/api/vms/appointments/{id}/actions", id).with(httpBasic("operator", "operator123"))
                .contentType(MediaType.APPLICATION_JSON).content("{\"action\":\"APPROVE\",\"remark\":\"接待人确认\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("安保复核"))
            .andExpect(jsonPath("$.data.passCode").doesNotExist());
        mvc.perform(post("/api/vms/appointments/{id}/actions", id).with(httpBasic("operator", "operator123"))
                .contentType(MediaType.APPLICATION_JSON).content("{\"action\":\"APPROVE\",\"remark\":\"越权安保复核\"}"))
            .andExpect(status().isForbidden());
        mvc.perform(post("/api/vms/appointments/{id}/actions", id).with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON).content("{\"action\":\"APPROVE\",\"remark\":\"材料缺失时不得放行\"}"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("合规校验未通过")));
        submitAndApproveDocument(id, "身份证明", "restricted-identity.pdf", "b".repeat(64));
        submitAndApproveDocument(id, "安全承诺书", "restricted-safety.pdf", "c".repeat(64));
        mvc.perform(post("/api/vms/appointments/{id}/actions", id).with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON).content("{\"action\":\"APPROVE\",\"remark\":\"安保复核通过\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("已审批"))
            .andExpect(jsonPath("$.data.passCode").isNotEmpty());
    }

    @Test void batchCapacityApprovalBoardAndOverstayInspectionWork() throws Exception {
        mvc.perform(post("/api/vms/appointments/batch").with(httpBasic("operator", "operator123"))
                .contentType(MediaType.APPLICATION_JSON).content("""
                    {"appointments":[
                    {"visitorName":"批量访客甲","visitorCompany":"合作伙伴甲","visitorPhone":"13812340003","hostName":"王诚","purpose":"批量接待测试","visitDate":"2026-09-05","timeSlot":"09:00-11:00","accessArea":"A座会议中心","visitorCount":2,"clientRequestId":"TEST-BATCH-001","siteCode":"SH-HQ"},
                    {"visitorName":"批量访客乙","visitorCompany":"合作伙伴乙","visitorPhone":"13812340004","hostName":"王诚","purpose":"批量接待测试","visitDate":"2026-09-05","timeSlot":"14:00-16:00","accessArea":"A座会议中心","visitorCount":3,"clientRequestId":"TEST-BATCH-002","siteCode":"SH-HQ"}
                    ]}
                    """))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.processed").value(2));
        mvc.perform(post("/api/vms/appointments").with(httpBasic("operator", "operator123"))
                .contentType(MediaType.APPLICATION_JSON).content("""
                    {"visitorName":"超容量访客","visitorCompany":"大型团队","visitorPhone":"13812340005",
                    "hostName":"王诚","purpose":"容量校验","visitDate":"2026-09-06",
                    "timeSlot":"09:00-11:00","accessArea":"A座会议中心","visitorCount":200}
                    """))
            .andExpect(status().isConflict());
        mvc.perform(get("/api/admin/vms/enterprise/approval-board").with(httpBasic("admin", "admin123")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.pending").isNumber())
            .andExpect(jsonPath("$.data.recentTasks").isArray());
        mvc.perform(get("/api/admin/vms/enterprise/approval-board").with(httpBasic("operator", "operator123")))
            .andExpect(status().isForbidden());
        mvc.perform(post("/api/admin/vms/enterprise/overstay-inspections").with(httpBasic("admin", "admin123")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.inspected").isNumber());
    }

    @Test void rejectedAppointmentCanBeEditedAndResubmittedWithRecalculatedRisk() throws Exception {
        var created = mvc.perform(post("/api/vms/appointments").with(httpBasic("operator", "operator123"))
                .contentType(MediaType.APPLICATION_JSON).content("""
                    {"visitorName":"重新送审访客","visitorCompany":"工程服务商","visitorPhone":"13812340006",
                    "hostName":"周敏","purpose":"首次申请","visitDate":"2026-09-07",
                    "timeSlot":"09:00-11:00","accessArea":"A座会议中心","visitorCount":2}
                    """))
            .andExpect(status().isOk()).andReturn();
        var matcher = java.util.regex.Pattern.compile("\\\"id\\\":(\\d+)")
            .matcher(created.getResponse().getContentAsString());
        org.junit.jupiter.api.Assertions.assertTrue(matcher.find());
        long id = Long.parseLong(matcher.group(1));
        mvc.perform(post("/api/vms/appointments/{id}/actions", id).with(httpBasic("operator", "operator123"))
                .contentType(MediaType.APPLICATION_JSON).content("{\"action\":\"REJECT\",\"remark\":\"补充区域信息\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("已驳回"));
        mvc.perform(put("/api/vms/appointments/{id}", id).with(httpBasic("operator", "operator123"))
                .contentType(MediaType.APPLICATION_JSON).content("""
                    {"visitorName":"重新送审访客","visitorCompany":"工程服务商","visitorPhone":"13812340006",
                    "hostName":"周敏","purpose":"补充后的申请","visitDate":"2026-09-07",
                    "timeSlot":"09:00-11:00","accessArea":"受限区-数据中心","visitorCount":2}
                    """))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("待审批"))
            .andExpect(jsonPath("$.data.riskLevel").value("关注"))
            .andExpect(jsonPath("$.data.approvalStage").value("接待人审批"));
    }

    @Test void enterpriseFieldOperationsPreventPassbackAndCloseTheBadgeLifecycle() throws Exception {
        var created = mvc.perform(post("/api/vms/appointments").with(httpBasic("operator", "operator123"))
                .contentType(MediaType.APPLICATION_JSON).content("""
                    {"visitorName":"现场通行测试访客","visitorCompany":"知华测试伙伴","visitorPhone":"13812340007",
                    "hostName":"王诚","purpose":"现场执行闭环测试","visitDate":"2026-09-08",
                    "timeSlot":"09:00-11:00","accessArea":"A座会议中心","visitorCount":1,
                    "clientRequestId":"TEST-FIELD-001","siteCode":"SH-HQ"}
                    """))
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        var idMatcher = java.util.regex.Pattern.compile("\\\"id\\\":(\\d+)").matcher(created);
        var noMatcher = java.util.regex.Pattern.compile("\\\"appointmentNo\\\":\\\"([^\\\"]+)").matcher(created);
        org.junit.jupiter.api.Assertions.assertTrue(idMatcher.find());
        org.junit.jupiter.api.Assertions.assertTrue(noMatcher.find());
        long id = Long.parseLong(idMatcher.group(1));
        String appointmentNo = noMatcher.group(1);

        mvc.perform(post("/api/vms/appointments/{id}/actions", id).with(httpBasic("operator", "operator123"))
                .contentType(MediaType.APPLICATION_JSON).content("{\"action\":\"APPROVE\",\"remark\":\"准入审批通过\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("已审批"));
        mvc.perform(post("/api/vms/appointments/{id}/badges", id).with(httpBasic("operator", "operator123"))
                .contentType(MediaType.APPLICATION_JSON).content("{\"badgeNo\":\"BG-SH-0002\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("已发放"))
            .andExpect(jsonPath("$.data.appointmentNo").value(appointmentNo));

        String inbound = "{\"appointmentNo\":\"" + appointmentNo + "\",\"gateCode\":\"GATE-01\",\"direction\":\"IN\"}";
        mvc.perform(post("/api/vms/access-events").with(httpBasic("operator", "operator123"))
                .contentType(MediaType.APPLICATION_JSON).content(inbound))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.allowed").value(true))
            .andExpect(jsonPath("$.data.appointment.status").value("已到访"));
        mvc.perform(post("/api/vms/access-events").with(httpBasic("operator", "operator123"))
                .contentType(MediaType.APPLICATION_JSON).content(inbound))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.allowed").value(false))
            .andExpect(jsonPath("$.data.message").value(org.hamcrest.Matchers.containsString("防重复入场")));

        String outbound = "{\"appointmentNo\":\"" + appointmentNo + "\",\"gateCode\":\"GATE-01\",\"direction\":\"OUT\"}";
        mvc.perform(post("/api/vms/access-events").with(httpBasic("operator", "operator123"))
                .contentType(MediaType.APPLICATION_JSON).content(outbound))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.allowed").value(true))
            .andExpect(jsonPath("$.data.appointment.status").value("已离场"));
        mvc.perform(post("/api/vms/access-events").with(httpBasic("operator", "operator123"))
                .contentType(MediaType.APPLICATION_JSON).content(outbound))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.allowed").value(false))
            .andExpect(jsonPath("$.data.message").value(org.hamcrest.Matchers.containsString("防重复离场")));

        mvc.perform(get("/api/vms/badges").with(httpBasic("operator", "operator123")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data[?(@.badgeNo == 'BG-SH-0002')].status").value("可用"));
        mvc.perform(get("/api/vms/access-events").with(httpBasic("operator", "operator123")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(4)));
        mvc.perform(post("/api/admin/vms/notifications/dispatch").with(httpBasic("admin", "admin123")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.sent").value(org.hamcrest.Matchers.greaterThan(0)));
        mvc.perform(get("/api/admin/vms/field-dashboard").with(httpBasic("admin", "admin123")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.deniedAccessEvents").value(org.hamcrest.Matchers.greaterThanOrEqualTo(2)));
        mvc.perform(get("/api/admin/vms/compliance/retention-preview").with(httpBasic("admin", "admin123")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.action").value(org.hamcrest.Matchers.containsString("不自动删除")));
        mvc.perform(get("/api/admin/vms/field-dashboard").with(httpBasic("operator", "operator123")))
            .andExpect(status().isForbidden());
    }

    @Test void multiSiteContractorComplianceMusterAndExportAreEnterpriseReady() throws Exception {
        mvc.perform(get("/api/vms/sites").with(httpBasic("operator", "operator123")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(2)));
        mvc.perform(post("/api/admin/vms/sites").with(httpBasic("operator", "operator123"))
                .contentType(MediaType.APPLICATION_JSON).content("""
                    {"siteCode":"HZ-FAC","siteName":"杭州制造基地","address":"杭州市测试地址",
                    "timezone":"Asia/Shanghai","slotCapacity":20,"assemblyPoint":"厂区东门集合点","status":"启用"}
                    """))
            .andExpect(status().isForbidden());
        mvc.perform(post("/api/admin/vms/sites").with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON).content("""
                    {"siteCode":"HZ-FAC","siteName":"杭州制造基地","address":"杭州市测试地址",
                    "timezone":"Asia/Shanghai","slotCapacity":20,"assemblyPoint":"厂区东门集合点","status":"启用"}
                    """))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.siteCode").value("HZ-FAC"));
        mvc.perform(post("/api/admin/vms/contractor-credentials").with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON).content("""
                    {"companyName":"企业合规测试工程有限公司","credentialType":"施工服务资质",
                    "credentialNo":"CERT-TEST-2026-001","validUntil":"2027-12-31",
                    "safetyTrainingCompleted":true,"status":"有效"}
                    """))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("有效"));

        var created = mvc.perform(post("/api/vms/appointments").with(httpBasic("operator", "operator123"))
                .contentType(MediaType.APPLICATION_JSON).content("""
                    {"visitorName":"承包商测试访客","visitorCompany":"企业合规测试工程有限公司","visitorPhone":"13812340008",
                    "hostName":"徐亮","purpose":"受限区设备施工","visitDate":"2026-09-09",
                    "timeSlot":"09:00-11:00","accessArea":"受限区-数据中心","visitorCount":3,
                    "clientRequestId":"TEST-COMPLIANCE-001","siteCode":"HZ-FAC"}
                    """))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.siteCode").value("HZ-FAC")).andReturn();
        var matcher = java.util.regex.Pattern.compile("\\\"id\\\":(\\d+)")
            .matcher(created.getResponse().getContentAsString());
        org.junit.jupiter.api.Assertions.assertTrue(matcher.find());
        long id = Long.parseLong(matcher.group(1));
        mvc.perform(post("/api/vms/appointments/{id}/actions", id).with(httpBasic("operator", "operator123"))
                .contentType(MediaType.APPLICATION_JSON).content("{\"action\":\"APPROVE\",\"remark\":\"接待人确认\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("安保复核"));
        submitAndApproveDocument(id, "身份证明", "contractor-identity.pdf", "d".repeat(64));
        submitAndApproveDocument(id, "安全承诺书", "contractor-safety.pdf", "e".repeat(64));
        submitAndApproveDocument(id, "作业人员清单", "contractor-roster.xlsx", "f".repeat(64));
        mvc.perform(get("/api/vms/appointments/{id}/compliance", id).with(httpBasic("operator", "operator123")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.ready").value(true))
            .andExpect(jsonPath("$.data.contractorWork").value(true));
        mvc.perform(post("/api/vms/appointments/{id}/actions", id).with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON).content("{\"action\":\"APPROVE\",\"remark\":\"资质和材料均通过\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("已审批"));

        mvc.perform(get("/api/admin/vms/emergency/muster?siteCode=SH-HQ").with(httpBasic("admin", "admin123")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.assemblyPoint").isNotEmpty())
            .andExpect(jsonPath("$.data.people").isArray());
        mvc.perform(get("/api/admin/vms/exports/appointments?siteCode=HZ-FAC&from=2026-09-01&to=2026-09-30")
                .with(httpBasic("admin", "admin123")))
            .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith("text/csv"))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("承包商测试访客")));
        mvc.perform(get("/api/admin/vms/exports/appointments?siteCode=HZ-FAC&from=2026-09-01&to=2026-09-30")
                .with(httpBasic("operator", "operator123")))
            .andExpect(status().isForbidden());
    }

    private long submitAndApproveDocument(long appointmentId, String type, String fileName, String checksum) throws Exception {
        var submitted = mvc.perform(post("/api/vms/appointments/{id}/documents", appointmentId)
                .with(httpBasic("operator", "operator123")).contentType(MediaType.APPLICATION_JSON)
                .content("{\"documentType\":\"" + type + "\",\"fileName\":\"" + fileName
                    + "\",\"checksum\":\"" + checksum + "\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("待审核"))
            .andReturn().getResponse().getContentAsString();
        var matcher = java.util.regex.Pattern.compile("\\\"id\\\":(\\d+)").matcher(submitted);
        org.junit.jupiter.api.Assertions.assertTrue(matcher.find());
        long documentId = Long.parseLong(matcher.group(1));
        mvc.perform(post("/api/admin/vms/documents/{id}/review", documentId).with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON).content("{\"approved\":true,\"comment\":\"测试审核通过\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("已通过"));
        return documentId;
    }
}
