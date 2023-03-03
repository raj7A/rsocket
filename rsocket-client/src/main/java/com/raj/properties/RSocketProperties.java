package com.raj.properties;

import lombok.Getter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.Objects;

@ConfigurationProperties(prefix = "com.rsocket")
@ConstructorBinding
@Getter
@ToString
public class RSocketProperties {

    private final boolean rSocketEnabled;
    private final boolean connectOnStart;
    private final String host;
    private final Integer port;
    private final String route;
    private final KeepAlive keepAlive;
    private final Reconnect reconnect;

    public RSocketProperties(boolean rSocketEnabled, boolean connectOnStart, String host, Integer port, String route, KeepAlive keepAlive, Reconnect reconnect) {
        this.rSocketEnabled = rSocketEnabled;
        this.connectOnStart = connectOnStart;
        this.host = Objects.isNull(host) ? RSocketConstants.HOST : host;
        this.route = Objects.isNull(route) ? RSocketConstants.DEFAULT_ROUTE : route;
        this.port = Objects.isNull(host) ? RSocketConstants.PORT : port;
        this.keepAlive = Objects.isNull(keepAlive) ? new KeepAlive(RSocketConstants.INTERVAL, RSocketConstants.MAX_LIFE_TIME) : keepAlive;
        this.reconnect = Objects.isNull(reconnect) ? new Reconnect(new Retry(RSocketConstants.MAX_ATTEMPTS, RSocketConstants.FIXED_DELAY)): reconnect;
    }

    @ConstructorBinding
    @Getter
    @ToString
    public static class KeepAlive {
        private final long interval;
        private final long maxLifeTime;

        public KeepAlive(long interval, long maxLifeTime) {
            this.interval =  isInvalid(interval) ? RSocketConstants.INTERVAL : interval;
            this.maxLifeTime = isInvalid(maxLifeTime) ? RSocketConstants.MAX_LIFE_TIME : maxLifeTime;
        }

    }

    @ConstructorBinding
    @Getter
    @ToString
    public static class Reconnect {
        private final Retry retry;

        public Reconnect(Retry retry) {
            this.retry = Objects.isNull(retry) ? new Retry(RSocketConstants.MAX_ATTEMPTS, RSocketConstants.FIXED_DELAY) : retry;
        }
    }

    @ConstructorBinding
    @Getter
    @ToString
    public static class Retry {
        private final long maxAttempts;
        private final long fixedDelay;

        public Retry(long maxAttempts, long fixedDelay) {
            this.maxAttempts = isInvalid(maxAttempts) ? RSocketConstants.MAX_ATTEMPTS : maxAttempts;
            this.fixedDelay = isInvalid(fixedDelay) ? RSocketConstants.FIXED_DELAY : fixedDelay;
        }
    }

    private static boolean isInvalid(long value) {
        return value <= 0;
    }
}