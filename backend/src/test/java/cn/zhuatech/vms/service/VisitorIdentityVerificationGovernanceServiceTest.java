/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
class VisitorIdentityVerificationGovernanceServiceTest {
    private final VisitorIdentityVerificationGovernanceService service = new VisitorIdentityVerificationGovernanceService();

    /** 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
    @Test
    void allowsVerifiedStandardVisitor() {
        var result = service.assess(request(95, false, false, true));
        assertThat(result.decision()).isEqualTo(VisitorIdentityVerificationGovernanceService.Decision.ALLOW);
        assertThat(result.accessProfile()).isEqualTo("STANDARD_VISITOR");
    }

    /** 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
    @Test
    void blocksWatchlistVisitor() {
        var result = service.assess(request(95, true, false, true));
        assertThat(result.decision()).isEqualTo(VisitorIdentityVerificationGovernanceService.Decision.BLOCKED);
        assertThat(result.blockers()).anyMatch(item -> item.contains("关注名单"));
    }

    /** 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
    @Test
    void reviewsLowFaceMatchRestrictedVisitor() {
        var result = service.assess(request(60, false, true, false));
        assertThat(result.decision()).isEqualTo(VisitorIdentityVerificationGovernanceService.Decision.MANUAL_REVIEW);
        assertThat(result.accessProfile()).isEqualTo("ESCORTED_RESTRICTED");
        assertThat(result.actions()).hasSize(2);
    }

    private VisitorIdentityVerificationGovernanceService.Request request(int score, boolean watchlist,
                                                                          boolean restricted,
                                                                          boolean prepared) {
        return new VisitorIdentityVerificationGovernanceService.Request("VERIFY-100", "APPT-100",
                true, true, false, watchlist, true, true, score, 80, restricted,
                true, true, false, true, true, prepared);
    }
}
