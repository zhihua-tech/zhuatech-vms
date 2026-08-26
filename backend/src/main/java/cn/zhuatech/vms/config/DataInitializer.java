/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.config;

import cn.zhuatech.vms.domain.DomainCatalog;
import cn.zhuatech.vms.model.WorkItem;
import cn.zhuatech.vms.repository.WorkItemRepository;
import cn.zhuatech.vms.model.Appointment;
import cn.zhuatech.vms.model.RiskAlert;
import cn.zhuatech.vms.model.SiteResource;
import cn.zhuatech.vms.model.SystemSetting;
import cn.zhuatech.vms.model.VisitorProfile;
import cn.zhuatech.vms.repository.AppointmentRepository;
import cn.zhuatech.vms.repository.RiskAlertRepository;
import cn.zhuatech.vms.repository.SiteResourceRepository;
import cn.zhuatech.vms.repository.SystemSettingRepository;
import cn.zhuatech.vms.repository.VisitorProfileRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner seedData(WorkItemRepository repository, DomainCatalog catalog) {
        return args -> {
            if (repository.count() == 0) {
                repository.saveAll(catalog.seedItems().stream()
                    .map(item -> new WorkItem(item.recordNo(), item.title(), item.status(), item.owner(), item.priority()))
                    .toList());
            }
        };
    }

    @Bean
    CommandLineRunner seedVms(AppointmentRepository appointments, VisitorProfileRepository visitors,
                              SiteResourceRepository resources, RiskAlertRepository alerts,
                              SystemSettingRepository settings) {
        return args -> {
            if (appointments.count() == 0) {
                appointments.saveAll(java.util.List.of(
                    new Appointment("VMS-20260826-101", "陈伟", "启明设备服务有限公司", "13800001231",
                        "周敏", "数据中心空调维保", LocalDate.now(), "14:00-16:00", "受限区-数据中心", 3, "待审批", "关注"),
                    new Appointment("VMS-20260826-102", "林悦", "远见咨询", "13900004562",
                        "王诚", "项目方案交流", LocalDate.now(), "10:00-11:30", "A座会议中心", 5, "已审批", "正常"),
                    new Appointment("VMS-20260826-103", "赵磊", "城运工程", "13600007894",
                        "徐亮", "弱电施工复检", LocalDate.now(), "09:00-18:00", "B座工程区", 8, "已到访", "关注"),
                    new Appointment("VMS-20260825-088", "孙静", "融科伙伴", "13700002216",
                        "吴晓", "合作洽谈", LocalDate.now().minusDays(1), "15:00-16:30", "A座会客区", 2, "已离场", "正常")));
            }
            if (visitors.count() == 0) {
                visitors.saveAll(java.util.List.of(
                    new VisitorProfile("陈伟", "启明设备服务有限公司", "13800001231", true, false, 6, LocalDateTime.now().minusDays(18), "设备维保服务商"),
                    new VisitorProfile("林悦", "远见咨询", "13900004562", true, false, 3, LocalDateTime.now().minusDays(7), "常规商务来访"),
                    new VisitorProfile("赵磊", "城运工程", "13600007894", true, false, 11, LocalDateTime.now(), "施工人员，需佩戴访客证"),
                    new VisitorProfile("高峰", "个人", "13500001999", false, true, 1, LocalDateTime.now().minusMonths(2), "证件信息不一致，待安保复核")));
            }
            if (resources.count() == 0) {
                resources.saveAll(java.util.List.of(
                    new SiteResource("接待人", "HOST-001", "周敏", "信息技术部", "启用"),
                    new SiteResource("接待人", "HOST-002", "王诚", "行政管理部", "启用"),
                    new SiteResource("访问区域", "AREA-A01", "A座会议中心", "行政管理部", "开放"),
                    new SiteResource("访问区域", "AREA-D01", "受限区-数据中心", "信息技术部", "审批开放"),
                    new SiteResource("门禁点", "GATE-01", "园区北门访客闸机", "安保中心", "在线"),
                    new SiteResource("门禁点", "GATE-02", "A座一层前台", "安保中心", "在线")));
            }
            if (alerts.count() == 0) {
                alerts.saveAll(java.util.List.of(
                    new RiskAlert("ALT-20260826-01", "超时未离场", "高", "施工访客已超预约时段 25 分钟",
                        "VMS-20260826-103", "待处理", "园区安保中心"),
                    new RiskAlert("ALT-20260826-02", "资料待补充", "中", "设备维保预约缺少施工人员附件",
                        "VMS-20260826-101", "待处理", "行政接待组")));
            }
            if (settings.count() == 0) {
                settings.saveAll(java.util.List.of(
                    new SystemSetting("siteName", "上海创新园区"),
                    new SystemSetting("approvalMode", "接待人审批 + 安保复核"),
                    new SystemSetting("passValidity", "预约时段前后 30 分钟"),
                    new SystemSetting("retentionDays", "180"),
                    new SystemSetting("notificationChannel", "站内消息")));
            }
        };
    }
}
