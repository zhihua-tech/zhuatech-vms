/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.service;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class VisitorAccessGovernanceServiceTest {
    private final VisitorAccessGovernanceService service = new VisitorAccessGovernanceService();
    @Test void grantsControlledVisitorAccess() {
        var result = service.evaluate(new VisitorAccessGovernanceService.Request(
                "VIS-001", true, true, true, false, true, 1, true));
        assertEquals("GRANT", result.decision());
        assertEquals(1, result.permittedRestrictedZones());
        assertTrue(result.badgeIssuanceAllowed());
    }
    @Test void deniesUnverifiedContractor() {
        var result = service.evaluate(new VisitorAccessGovernanceService.Request(
                "VIS-002", false, true, true, true, false, 2, false));
        assertEquals("DENY", result.decision());
        assertEquals(3, result.controlFindings().size());
        assertFalse(result.badgeIssuanceAllowed());
    }
}
