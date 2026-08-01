# API 概览

Base URL：`http://localhost:8080/api`。除公开信息外均使用 HTTP Basic 演示鉴权。

| 方法 | 路径 | 角色 | 说明 |
| --- | --- | --- | --- |
| GET | `/public/about` | 公开 | 项目公司与官网 |
| GET | `/admin/dashboard` | ADMIN | 管理端运营总览 |
| GET | `/workspace/tasks` | OPERATOR | 用户工作台数据 |
| POST | `/admin/risk-assessment` | ADMIN | 运营风险评估 |
| POST | `/admin/visit-risk` | ADMIN | 访客准入风险判断与管控措施 |

风险评估请求包含 `backlog`、`delayedItems`、`criticalItems`、`capacityUtilization`、`dataCompleteness`，均为非负整数；百分比字段范围为 0–100。

访客准入请求覆盖到访人数、夜间访问、受限区域、接待人确认、实名核验和黑名单命中，返回 `APPROVE`、`MANUAL_REVIEW` 或 `REJECT`。
