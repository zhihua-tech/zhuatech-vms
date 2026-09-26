# 访客身份核验治理

`POST /api/enterprise/vms/visitor-identity-verification` 将访客放行条件收敛为单一决策接口。

- 验证有效预约、身份证件、被访人确认、隐私同意和关注名单。
- 人证比对低于阈值时转人工双证件复核。
- 限制区域强制安全告知与全程陪同，未成年访客要求监护人同意。

返回 `ALLOW / MANUAL_REVIEW / BLOCKED` 与可执行通行配置。
