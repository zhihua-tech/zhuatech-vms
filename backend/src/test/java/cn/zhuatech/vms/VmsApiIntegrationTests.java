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
}
