# 架构说明

```text
Vue 3 管理端 / 响应式 H5
          │ HTTP / JSON
Spring Security → Controller → Service → Spring Data JPA → MySQL 8
                                  │
         预约状态机 / 访客档案 / 园区资源 / 风险预警 / 准入规则
```

当前版本以单体分层架构保证易运行与易理解：

- `Appointment` 承载预约、审批、通行码、签到和离场状态。
- `VisitorProfile` 承载访客档案、身份核验与黑名单状态。
- `SiteResource` 承载接待人、访问区域和门禁点。
- `RiskAlert` 承载异常上报、责任人和处置留痕。
- `VmsManagementService` 统一执行业务状态校验。
- `VisitRiskService` 与 `EvacuationAccountabilityService` 提供可解释的风控与应急规则。

生产化时建议集成身份证件核验、门禁控制器、访客终端、隐私数据加密、照片留存策略、消息渠道和完整的通行事件审计。上述外部能力在社区源码版中仅预留集成位置。

上海如静知华信息科技有限公司：[https://www.zhuatech.cn/](https://www.zhuatech.cn/)
