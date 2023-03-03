package com.raj.properties;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RSocketPropertiesTest {

    @Test
    void defaultPropertiesIsUsedIfInvalidValuesPassed() {
        RSocketProperties rSocketProperties = new RSocketProperties(false, false, null, null, null, null, null);

        Assertions.assertEquals(RSocketConstants.HOST, rSocketProperties.getHost());
        Assertions.assertEquals(RSocketConstants.PORT, rSocketProperties.getPort());
        Assertions.assertEquals(RSocketConstants.DEFAULT_ROUTE, rSocketProperties.getRoute());
        Assertions.assertEquals(RSocketConstants.INTERVAL, rSocketProperties.getKeepAlive().getInterval());
        Assertions.assertEquals(RSocketConstants.MAX_LIFE_TIME, rSocketProperties.getKeepAlive().getMaxLifeTime());
        Assertions.assertEquals(RSocketConstants.FIXED_DELAY, rSocketProperties.getReconnect().getRetry().getFixedDelay());
        Assertions.assertEquals(RSocketConstants.MAX_ATTEMPTS, rSocketProperties.getReconnect().getRetry().getMaxAttempts());
    }
}