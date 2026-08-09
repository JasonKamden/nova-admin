package org.dromara.nova.common.tenant.context;

import org.dromara.nova.common.core.enums.ContextType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TenantContextHolderTest {
    @AfterEach
    void cleanup() {
        TenantContextHolder.clear();
    }

    @Test
    void storesTenantContext() {
        TenantContextHolder.set(new TenantContext(ContextType.TENANT, 1001L));
        Assertions.assertEquals(1001L, TenantContextHolder.get().tenantId());
    }
}
