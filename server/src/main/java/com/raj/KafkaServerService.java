package com.raj;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class KafkaServerService {

    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String TOPIC = "cust-topic";
    private final KafkaSender<String, String> sender;
    private static AtomicInteger processed = new AtomicInteger();

    public KafkaServerService() {

        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.RETRIES_CONFIG, 2);
        props.put(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG, 5000);
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 5000);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "sample-producer");
        props.put(ProducerConfig.ACKS_CONFIG, "0");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        SenderOptions<String, String> senderOptions = SenderOptions.create(props);

        sender = KafkaSender.create(senderOptions);
    }

    public Disposable send(Customer customer) {
        ProducerRecord<String, String> objectStringProducerRecord =
                new ProducerRecord<>(TOPIC, customer.toString());
        SenderRecord<String, String, Object> senderRecord = SenderRecord.create(objectStringProducerRecord, null);
        return sender.send(Mono.just(senderRecord))
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
                        public Object correlationMetadata() {
                            return null;
                        }
                    });
                }).flatMap(response -> {
                    //System.out.println(response.recordMetadata().toString());
                    if (response.exception() != null && "not sent".equalsIgnoreCase(response.exception().getMessage())) {
                        System.out.println(response.exception().getMessage());
                        return Mono.just(response.exception().getMessage());
                    }
                    System.out.println("Data sent to save : " + customer.getCustomerId());
                    System.out.println("processed : " + processed.incrementAndGet());
                    return Mono.just("sent");
                }).subscribe();
    }

}
