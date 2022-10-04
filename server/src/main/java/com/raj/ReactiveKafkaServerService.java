package com.raj;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderResult;

@Service
public class ReactiveKafkaServerService {

    private final ReactiveKafkaProducerTemplate<String, Object> reactiveKafkaProducerTemplate;
    private static final String TOPIC = "cust-topic";

    public ReactiveKafkaServerService(ReactiveKafkaProducerTemplate<String, Object> reactiveKafkaProducerTemplate) {
        this.reactiveKafkaProducerTemplate = reactiveKafkaProducerTemplate;
    }

    public void send(Object data) {
        reactiveKafkaProducerTemplate.send(TOPIC, data)
                .onErrorResume(error -> {
            System.err.println(error.toString());
            return Mono.just(new SenderResult<>() {
                @Override
                public RecordMetadata recordMetadata() {
                    return null;
                }
                @Override
                public Exception exception() {
                    return new Exception("not sent");
                }
                @Override
                public Void correlationMetadata() {
                    return null;
                }
            });
        }).flatMap(response -> {
            //System.out.println(response.recordMetadata().toString());
            if (response.exception() != null && "not sent".equalsIgnoreCase(response.exception().getMessage())) {
                System.out.println(response.exception().getMessage());
                return Mono.just(response.exception().getMessage());
            }
            System.out.println("Data sent to save : " + data);
            return Mono.just("sent");
        }).subscribe();
    }
}