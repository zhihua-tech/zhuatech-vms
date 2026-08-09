# ZhuaTech VMS · 访客预约与通行管理

> 知华科技（上海如静知华信息科技有限公司）发布的访客管理社区源码项目。官网：[https://www.zhuatech.cn/](https://www.zhuatech.cn/)

![VMS 园区接待管理端](docs/images/vms-reception-dashboard.png)

## 从预约到离场，一条可追踪的访问链路

ZhuaTech VMS 将预约提交、接待人审批、身份核验、通行授权、在园关注和离场确认放入同一个前后端分离样例。管理端服务于前台与安保中心，移动端服务于接待人和访客，可用于学习园区访客业务、权限隔离和响应式 H5 实现。

| 流程阶段 | 系统能力 | 责任角色 |
| --- | --- | --- |
| 预约前 | 到访事由、人员名单、时间和区域登记 | 访客 / 接待人 |
| 到访前 | 接待确认、实名核验、准入风险评估 | 接待人 / 安保 |
| 在园中 | 一次性通行码、区域限制、超时提醒 | 前台 / 安保 |
| 离场后 | 通行回收、离场确认、异常留痕 | 前台 / 管理员 |

![VMS 移动访客通行工作台](docs/images/vms-mobile-pass.png)

<p align="center"><em>移动端：预约、签到、通行码和接待提醒的专业 H5 工作台</em></p>

### 已实现内容

- Spring Boot 4、Java 21、Spring Security、JPA 和 MySQL 后端。
- Vue 3、Vite 管理端与响应式移动端。
- 运营总览、预约事项、接待协同和演示数据。
- `POST /api/admin/visit-risk` 访客准入规则，输出 `APPROVE / MANUAL_REVIEW / REJECT`。
- Docker Compose、自动化测试、API/架构文档和社区协作规范。

### 快速体验

```bash
cp .env.example .env
docker compose up --build
```

打开 `http://localhost:8090`。本地演示账号：`admin / admin123`、`operator / operator123`。这些默认值不得用于联网或生产环境。

```bash
curl -u admin:admin123 -H 'Content-Type: application/json' \
  -d '{"visitorCount":8,"afterHours":true,"restrictedArea":true,"hostConfirmed":true,"identityVerified":false,"blacklistHit":false}' \
  http://localhost:8080/api/admin/visit-risk
```

更多信息参见 [API](docs/API.md)、[架构](docs/ARCHITECTURE.md) 和 [安全政策](SECURITY.md)。

## 许可边界

本工程仅能用于个人非商业学习、研究和技术交流。未经上海如静知华信息科技有限公司书面授权，不得用于企业内部生产、商业部署、SaaS、收费下载、售卖、外包交付、投标、品牌替换或任何直接、间接商业用途。本许可含非商业限制，因此不是 OSI 认可的开源许可证，详情见 [LICENSE](LICENSE)。

## 商业授权与深度定制

需要对接证件核验、闸机门禁、企业微信、访客机或多园区能力，可访问[知华科技官网](https://www.zhuatech.cn/)，也可扫描任一微信二维码咨询。

<p align="center">
  <img src="docs/images/zhuatech-wechat-consulting.png" alt="知华科技微信咨询二维码一" width="250" />
  <img src="docs/images/zhuatech-wechat-consulting-2.png" alt="知华科技微信咨询二维码二" width="250" />
</p>

关键词：知华科技 VMS、访客管理系统、访客预约、园区门禁、访客通行码、Java 访客系统、Vue 访客管理、上海软件开发。

## 应急疏散访客清点

新增 `POST /api/vms/insights/evacuation-accountability`，根据已入场、已离场、集合点签到和接待人确认人数计算在场及未清点访客，输出 `CLEAR / RECONCILE / LOCATE_NOW`。紧急状态下可直接生成联系接待人、核对最后门禁位置和现场查找动作。
