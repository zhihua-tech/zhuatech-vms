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
| POST | `/vms/appointments/batch` | OPERATOR | 批量创建最多 50 条预约 |
| PUT | `/vms/appointments/{id}` | OPERATOR | 修改待审批或已驳回预约 |
| POST | `/vms/appointments/{id}/actions` | OPERATOR | 审批、驳回、取消、签到或离场 |
| POST | `/vms/passes/verify` | OPERATOR | 按预约编号或通行码核验通行凭证 |
| GET | `/vms/approval-tasks` | OPERATOR | 查询结构化审批任务及处理记录 |
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
| GET | `/admin/vms/enterprise/approval-board` | ADMIN | 审批待办、超时、安保复核和高风险看板 |
| POST | `/admin/vms/enterprise/overstay-inspections` | ADMIN | 执行在园超时巡检并生成风险预警 |
| GET | `/vms/badges` | OPERATOR | 查询访客证库存、领用和挂失状态 |
| POST | `/vms/appointments/{id}/badges` | OPERATOR | 为已审批或已到访预约发放访客证 |
| POST | `/vms/badges/{id}/actions` | OPERATOR | 归还或挂失访客证 |
| GET | `/vms/access-events` | OPERATOR | 查询最近 100 条门禁通行事件 |
| POST | `/vms/access-events` | OPERATOR | 上报入场/离场事件并执行实时准入判断 |
| GET | `/admin/vms/field-dashboard` | ADMIN | 访客证、拒绝通行和通知异常指标 |
| GET | `/admin/vms/notifications` | ADMIN | 查询最近 100 条可靠通知任务 |
| POST | `/admin/vms/notifications/dispatch` | ADMIN | 执行待发送及到期失败通知派发 |
| POST | `/admin/vms/notifications/{id}/retry` | ADMIN | 将失败通知置为立即重试 |
| GET | `/admin/vms/compliance/retention-preview` | ADMIN | 按留存策略预检超期数据影响范围 |

风险评估请求包含 `backlog`、`delayedItems`、`criticalItems`、`capacityUtilization`、`dataCompleteness`，均为非负整数；百分比字段范围为 0–100。

访客准入请求覆盖到访人数、夜间访问、受限区域、接待人确认、实名核验和黑名单命中，返回 `APPROVE`、`MANUAL_REVIEW` 或 `REJECT`。

预约操作请求示例：

```json
{"action":"APPROVE","remark":"接待人已确认"}
```

支持的 `action` 为 `APPROVE`、`REJECT`、`CANCEL`、`CHECK_IN` 和 `CHECK_OUT`。服务端校验状态顺序，非法流转返回 HTTP 409。普通预约一次通过后签发通行码；受限区域或 8 人以上预约第一次通过后进入“安保复核”，第二次审批必须使用 ADMIN 角色，职责越权返回 HTTP 403。

创建预约可选传 `clientRequestId` 和 `siteCode`。同一 `clientRequestId` 重试会返回第一次创建的预约，不会重复生成业务记录。批量接口请求结构如下：

```json
{"appointments":[{"visitorName":"张三","visitorCompany":"合作伙伴","visitorPhone":"13800000000","hostName":"李经理","purpose":"项目交流","visitDate":"2026-09-01","timeSlot":"09:00-11:00","accessArea":"A座会议中心","visitorCount":2,"clientRequestId":"ERP-20260901-001","siteCode":"SH-HQ"}]}
```

通行凭证核验请求可传预约编号或通行码：

```json
{"credential":"VMS-20260826-102"}
```

身份核验请求通过 `verified` 控制核验状态；资源请求需提供唯一 `resourceCode`、资源名称、类型、位置和状态。设置数据已持久化到数据库，服务重启后不会恢复为演示默认值。关键预约流转、访客风控、身份核验、资源变更、预警处置和设置修改会写入审计日志。

门禁控制器或边缘网关可使用预约编号上报事件。`direction` 仅允许 `IN` 或 `OUT`；接口始终返回结构化准入决策，拒绝事件同样持久化，便于识别重复进出和名单冻结：

```json
{"appointmentNo":"VMS-20260826-102","gateCode":"GATE-01","direction":"IN"}
```

访客证操作的 `action` 支持 `RETURN` 与 `REPORT_LOST`。通知任务采用应用内 outbox 基线：站内消息可本地完成派发，外部通道未配置时保留失败原因、尝试次数和下次重试时间，部署方可替换为企业微信、短信或自有消息适配器。

> 上海如静知华信息科技有限公司 · [https://www.zhuatech.cn/](https://www.zhuatech.cn/)
