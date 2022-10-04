package com.raj;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.cbor.Jackson2CborDecoder;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.DefaultMetadataExtractor;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.stereotype.Controller;
import org.springframework.util.MimeType;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderOptions;
import reactor.netty.http.HttpDecoderSpec;

import java.util.Map;

@Configuration
public class ServerConfig {

    @Bean
    DefaultMetadataExtractor extractor(RSocketStrategies rSocketStrategies) {
        DefaultMetadataExtractor extractor = new DefaultMetadataExtractor(rSocketStrategies.decoders());
        extractor.metadataToExtract(MimeType.valueOf("message/x.rsocket.composite-metadata.v0"), String.class, "message/x.rsocket.composite-metadata.v0");
        return extractor;
    }

//    @Bean
//    RSocketStrategies strategies() {
//        return RSocketStrategies.builder()
//                .decoders(decoders -> decoders.add(new Jackson2CborDecoder()))
//                .build();
//    }

    @Configuration
    public class ReactiveKafkaProducerConfig {
        @Bean
        public ReactiveKafkaProducerTemplate<String, Object> reactiveKafkaProducerTemplate(
                KafkaProperties properties) {
            Map<String, Object> props = properties.buildProducerProperties();
            props.put("acks", "all");

            return new ReactiveKafkaProducerTemplate<String, Object>(SenderOptions.create(props));
        }
    }

}