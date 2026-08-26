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
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.length()").value(4));
        mvc.perform(get("/api/vms/resources").with(httpBasic("operator", "operator123")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.length()").value(6));
        mvc.perform(get("/api/vms/alerts").with(httpBasic("operator", "operator123")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.length()").value(2));
        mvc.perform(get("/api/admin/vms/settings").with(httpBasic("admin", "admin123")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.siteName").value("上海创新园区"));
        mvc.perform(get("/api/admin/vms/settings").with(httpBasic("operator", "operator123")))
            .andExpect(status().isForbidden());
    }

    @Test void invalidAppointmentTransitionReturnsConflict() throws Exception {
        mvc.perform(post("/api/vms/appointments/1/actions").with(httpBasic("operator", "operator123"))
                .contentType(MediaType.APPLICATION_JSON).content("{\"action\":\"CHECK_IN\",\"remark\":\"跳过审批\"}"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.success").value(false));
    }
}
