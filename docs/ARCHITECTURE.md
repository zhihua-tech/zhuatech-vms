# 架构说明

```text
Vue 3 管理端 / 响应式 H5
          │ HTTP / JSON
Spring Security → Controller → Service → Spring Data JPA → MySQL 8
                                  │
                    访客准入与通行风险规则引擎
```

当前版本以单体分层架构保证易运行与易理解。`DomainCatalog` 管理预约与接待样例，`VisitRiskService` 执行准入规则，`WorkItem` 承载来访事项。生产化时建议集成身份证件核验、门禁控制器、访客终端、隐私数据加密、照片留存策略和通行事件审计。
