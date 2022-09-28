package com.raj;

import io.rsocket.core.RSocketClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.cbor.Jackson2CborEncoder;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import reactor.util.retry.Retry;

import java.time.Duration;

@Configuration
public class ClientConfig {

    @Bean("rSocketRequester1")
    protected RSocketRequester rSocketRequester1(RSocketStrategies rSocketStrategies,
            RSocketRequester.Builder builder) {

        return builder
                //.dataMimeType(MimeType.valueOf("application/cbor"))
                //.metadataMimeType(MimeType.valueOf("application/cbor"))
                .rsocketStrategies(rSocketStrategies)
                .rsocketConnector(connector ->
                        //connector.keepAlive(Duration.ofSeconds(3), Duration.ofSeconds(10)).
                        connector.reconnect(Retry.fixedDelay(2, Duration.ofSeconds(10))))
                .tcp("localhost", 9090);
    }

    @Bean("rSocketRequester2")
    protected RSocketRequester rSocketRequester2(RSocketStrategies rSocketStrategies,
            RSocketRequester.Builder builder) {

        return builder
                //.dataMimeType(MimeType.valueOf("application/cbor"))
                //.metadataMimeType(MimeType.valueOf("application/cbor"))
                .rsocketStrategies(rSocketStrategies)
                .rsocketConnector(connector ->
                        connector.keepAlive(Duration.ofSeconds(1), Duration.ofSeconds(100))
                                .reconnect(Retry.fixedDelay(20, Duration.ofSeconds(10))))
                .tcp("localhost", 9090);
    }

    @Bean("rSocketClient")
    protected RSocketClient rSocketClient(RSocketStrategies rSocketStrategies,
                                          RSocketRequester.Builder builder) {

        return builder
                .rsocketStrategies(rSocketStrategies)
                .rsocketConnector(connector ->
                        connector.reconnect(Retry.fixedDelay(2, Duration.ofSeconds(10))))
                .setupRoute("vz.customer")
                .tcp("localhost", 9090)
                .rsocketClient();
    }

    //@Bean
    protected RSocketStrategies rSocketStrategy() {
        return RSocketStrategies.builder()
                .encoders(encoders -> {
                    encoders.add(new Jackson2CborEncoder());
                })
                .build();
    }

}