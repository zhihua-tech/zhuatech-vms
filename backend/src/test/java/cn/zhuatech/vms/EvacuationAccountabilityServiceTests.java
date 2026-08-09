/* Copyright 2026 上海如静知华信息科技有限公司 */
package cn.zhuatech.vms;

import cn.zhuatech.vms.service.EvacuationAccountabilityService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EvacuationAccountabilityServiceTests {
    private final EvacuationAccountabilityService service = new EvacuationAccountabilityService();

    @Test
    void locatesUnaccountedVisitorsDuringEmergency() {
        var result = service.reconcile(new EvacuationAccountabilityService.Request(
            "SH-OFFICE", 30, 5, 18, 3, true));

        assertEquals(25, result.visitorsOnSite());
        assertEquals(4, result.unaccountedVisitors());
        assertEquals("LOCATE_NOW", result.decision());
    }

    @Test
    void clearsFullyAccountedSite() {
        var result = service.reconcile(new EvacuationAccountabilityService.Request(
            "SZ-LAB", 12, 2, 8, 2, true));

        assertEquals("CLEAR", result.decision());
    }
}
