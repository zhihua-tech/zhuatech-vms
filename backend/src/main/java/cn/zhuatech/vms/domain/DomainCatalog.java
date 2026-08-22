/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.domain;
import org.springframework.stereotype.Component;
import java.util.List;
@Component public class DomainCatalog {
    public String systemName(){return "知华 VMS 访客预约与通行管理平台";}
    public String sceneName(){return "访客预约、到访审批、证件核验、门禁通行与离场闭环";}
    public List<SeedItem> seedItems(){return List.of(
        new SeedItem("VMS-20260801-001","数据中心设备维保人员到访","处理中","园区安保中心","高"),
        new SeedItem("VMS-20260801-002","客户代表团会议预约","待处理","行政接待组","中"),
        new SeedItem("VMS-20260801-003","夜间施工人员名单复核","处理中","工程管理部","紧急"),
        new SeedItem("VMS-20260801-004","昨日访客离场记录核验","已完成","前台服务组","低"));}
    public List<String> recommendedActions(){return List.of("复核夜间和受限区域预约","确认接待人与访客身份信息","跟进超时未离场和通行异常记录");}
    public record SeedItem(String recordNo,String title,String status,String owner,String priority){}
}
