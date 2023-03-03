package com.raj.configs;

import com.raj.base.RSocketClient;
import com.raj.properties.RSocketProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import reactor.util.retry.Retry;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties(RSocketProperties.class)
//@ConditionalOnBean(RSocketProperties.class)
//@ConditionalOnProperty(prefix = "ofs", name = "rsocket.enabled", havingValue = "true")
public class RSocketConfiguration {

    private final RSocketProperties rSocketProperties;

    public RSocketConfiguration(RSocketProperties rSocketProperties) {
        this.rSocketProperties = rSocketProperties;
    }

    @Bean
    protected RSocketRequester rSocketRequester(RSocketStrategies rSocketStrategies,
                                                RSocketRequester.Builder builder) {
        return builder
                .rsocketStrategies(rSocketStrategies)
                .rsocketConnector(connector ->
                        connector
                                .keepAlive(Duration.ofMillis(rSocketProperties.getKeepAlive().getInterval()), Duration.ofMillis(rSocketProperties.getKeepAlive().getMaxLifeTime()))
                                .reconnect(Retry.fixedDelay(rSocketProperties.getReconnect().getRetry().getMaxAttempts(), Duration.ofMillis(rSocketProperties.getReconnect().getRetry().getFixedDelay()))))
                .tcp(rSocketProperties.getHost(), rSocketProperties.getPort());
    }

    @Bean
    protected RSocketClient rSocketClient(RSocketRequester rSocketRequester) {
        return new RSocketClient<>(rSocketRequester, rSocketProperties);
    }

}
