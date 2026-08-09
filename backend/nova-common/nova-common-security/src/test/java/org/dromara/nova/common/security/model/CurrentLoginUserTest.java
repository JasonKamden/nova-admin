package org.dromara.nova.common.security.model;

import org.dromara.nova.common.core.enums.ContextType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CurrentLoginUserTest {
    @Test
    void defensivelyCopiesRoleAndPermissionLists() {
        List<String> roles = new ArrayList<>(List.of("user"));
        CurrentLoginUser user = new CurrentLoginUser(1L, "u", "U", null, false, ContextType.TENANT, 10L, "T", null, null, roles, List.of("system:user:list"));
        roles.add("admin");
        assertEquals(List.of("user"), user.roles());
        assertThrows(UnsupportedOperationException.class, () -> user.roles().add("x"));
    }
}
