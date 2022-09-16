package com.raj;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.cbor.Jackson2CborEncoder;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import reactor.util.retry.Retry;

import java.time.Duration;

@Configuration
public class ClientConfig {

    @Bean
    protected RSocketRequester rSocketRequester(RSocketStrategies rSocketStrategies,
            RSocketRequester.Builder builder) {
//        rSocketStrategies.encoders().stream().forEach(enc ->{
//            System.out.println(enc.toString());
//            enc.getEncodableMimeTypes().stream().forEach(mt -> {
//                System.out.println(mt.toString());
//            });
//        });

        return builder
                //.dataMimeType(MimeType.valueOf("application/cbor"))
                //.metadataMimeType(MimeType.valueOf("application/cbor"))
                .rsocketStrategies(rSocketStrategies)
                .rsocketConnector(connector ->
                        connector.reconnect(Retry.fixedDelay(20, Duration.ofSeconds(10))))
                .tcp("localhost", 9090);
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