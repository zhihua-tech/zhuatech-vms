# API 概览

Base URL：`http://localhost:8080/api`。除公开信息外均使用 HTTP Basic 演示鉴权。

| 方法 | 路径 | 角色 | 说明 |
| --- | --- | --- | --- |
| GET | `/public/about` | 公开 | 项目公司与官网 |
| GET | `/admin/dashboard` | ADMIN | 管理端运营总览 |
| GET | `/workspace/tasks` | OPERATOR | 用户工作台数据 |
| POST | `/admin/risk-assessment` | ADMIN | 运营风险评估 |
| POST | `/admin/visit-risk` | ADMIN | 访客准入风险判断与管控措施 |
| GET | `/vms/overview` | OPERATOR | 预约、在园访客和预警汇总 |
| GET | `/vms/appointments` | OPERATOR | 预约列表 |
| POST | `/vms/appointments` | OPERATOR | 新建预约 |
| PUT | `/vms/appointments/{id}` | OPERATOR | 修改待审批或已驳回预约 |
| POST | `/vms/appointments/{id}/actions` | OPERATOR | 审批、驳回、取消、签到或离场 |
| POST | `/vms/passes/verify` | OPERATOR | 按预约编号或通行码核验通行凭证 |
| GET | `/vms/visitors` | OPERATOR | 访客档案列表 |
| POST | `/admin/vms/visitors/{id}/blacklist` | ADMIN | 加入或解除黑名单 |
| POST | `/vms/visitors/{id}/identity` | OPERATOR | 标记或撤销访客身份核验 |
| GET | `/vms/resources` | OPERATOR | 接待人、访问区域和门禁点 |
| POST | `/admin/vms/resources` | ADMIN | 新建接待人、访问区域或门禁资源 |
| PUT | `/admin/vms/resources/{id}` | ADMIN | 修改园区资源 |
| DELETE | `/admin/vms/resources/{id}` | ADMIN | 删除园区资源 |
| GET | `/vms/alerts` | OPERATOR | 风险预警列表 |
| POST | `/vms/alerts` | OPERATOR | 上报异常 |
| POST | `/vms/alerts/{id}/resolve` | OPERATOR | 完成预警处置并留痕 |
| GET | `/admin/vms/settings` | ADMIN | 读取基础设置 |
| PUT | `/admin/vms/settings` | ADMIN | 更新园区、审批、通行、通知和留存设置 |
| GET | `/admin/vms/reports/operations` | ADMIN | 预约、访客和资源运营报表 |
| GET | `/admin/vms/audit-logs` | ADMIN | 最近 100 条关键操作审计日志 |

风险评估请求包含 `backlog`、`delayedItems`、`criticalItems`、`capacityUtilization`、`dataCompleteness`，均为非负整数；百分比字段范围为 0–100。

访客准入请求覆盖到访人数、夜间访问、受限区域、接待人确认、实名核验和黑名单命中，返回 `APPROVE`、`MANUAL_REVIEW` 或 `REJECT`。

预约操作请求示例：

```json
{"action":"APPROVE","remark":"接待人已确认"}
```

支持的 `action` 为 `APPROVE`、`REJECT`、`CANCEL`、`CHECK_IN` 和 `CHECK_OUT`。服务端校验状态顺序，非法流转返回 HTTP 409。

通行凭证核验请求可传预约编号或通行码：

```json
{"credential":"VMS-20260826-102"}
```

身份核验请求通过 `verified` 控制核验状态；资源请求需提供唯一 `resourceCode`、资源名称、类型、位置和状态。设置数据已持久化到数据库，服务重启后不会恢复为演示默认值。关键预约流转、访客风控、身份核验、资源变更、预警处置和设置修改会写入审计日志。

> 上海如静知华信息科技有限公司 · [https://www.zhuatech.cn/](https://www.zhuatech.cn/)
