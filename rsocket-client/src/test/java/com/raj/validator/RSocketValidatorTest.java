package com.raj.validator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RSocketValidatorTest {

    @Test
    void isAbleToProcess() {
        assertTrue(RSocketValidator.isGoodToProcess("test", "data", Boolean.TRUE));
    }

    @Test
    void isNotAbleToProcessWhenRouteIsInvalid() {
        assertFalse(RSocketValidator.isGoodToProcess(null, "data", Boolean.TRUE));
    }

    @Test
    void isNotAbleToProcessWhenDataIsInvalid() {
        assertFalse(RSocketValidator.isGoodToProcess("test", null, Boolean.TRUE));
    }
}