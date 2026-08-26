# 架构说明

```text
Vue 3 管理端 / 响应式 H5
          │ HTTP / JSON
Spring Security → Controller → Service → Spring Data JPA → MySQL 8
                                  │
预约状态机 / 访客档案 / 园区资源 / 风险预警 / 持久化设置 / 操作审计
```

当前版本以单体分层架构保证易运行与易理解：

- `Appointment` 承载预约、审批、通行码、签到和离场状态。
- `VisitorProfile` 承载访客档案、身份核验与黑名单状态。
- `SiteResource` 承载接待人、访问区域和门禁点。
- `RiskAlert` 承载异常上报、责任人和处置留痕。
- `SystemSetting` 以键值形式持久化园区、审批、通行、通知和留存配置。
- `AuditLog` 记录模块、操作、业务编号、操作者、结果说明和时间，供管理员追溯关键变更。
- `VmsManagementService` 统一执行业务状态校验。
- `VisitRiskService` 与 `EvacuationAccountabilityService` 提供可解释的风控与应急规则。

生产化时建议集成权威身份证件核验、门禁控制器、访客终端、隐私数据加密、照片留存策略和消息渠道。社区源码版已提供人工身份核验、凭证校验和应用层操作审计；硬件通行事件、真实证件服务及第三方消息能力保留集成位置。

上海如静知华信息科技有限公司：[https://www.zhuatech.cn/](https://www.zhuatech.cn/)
