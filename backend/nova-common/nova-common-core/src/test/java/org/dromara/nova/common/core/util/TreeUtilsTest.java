package org.dromara.nova.common.core.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

class TreeUtilsTest {
    @Test
    void detectsCycle() {
        Map<Long, Long> p = Map.of(2L, 1L, 3L, 2L);
        Assertions.assertTrue(TreeUtils.wouldCreateCycle(1L, 3L, p::get));
        Assertions.assertFalse(TreeUtils.wouldCreateCycle(4L, 3L, p::get));
    }
}
