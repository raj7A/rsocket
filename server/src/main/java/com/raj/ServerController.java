package com.raj;

import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.Map;

@Controller
public class ServerController {

    private final DbServerService dbServerService;
    private final KafkaServerService kafkaServerService;
    private final ReactiveKafkaServerService reactiveKafkaServerService;
    public ServerController(DbServerService dbServerService, KafkaServerService kafkaServerService, ReactiveKafkaServerService reactiveKafkaServerService) {
        this.dbServerService = dbServerService;
        this.kafkaServerService = kafkaServerService;
        this.reactiveKafkaServerService = reactiveKafkaServerService;
    }

    @MessageMapping("fnf.customer")
    public void dBReceive(@Payload Mono<Customer> customerMono, @Headers Map<String, Object> metadata) {
        customerMono.flatMap(customer -> {
            System.out.println("Data received by fnf db : " + customer.getCustomerId());
            return dbServerService.send(customer);
        }).subscribe();
    }
    @MessageMapping("fnf")
    public void fnfReceive(@Payload Mono<String> dataMono, @Headers Map<String, Object> metadata) {
        dataMono.flatMap(data -> {
            System.out.println("Data received by fnf db : " + data);
            return Mono.empty();
        }).subscribe();
    }

    @MessageMapping("fnf.k.customer")
    public void kafkaReceive(@Payload Mono<Object> customerMono, @Headers Map<String, Object> metadata) {
        customerMono.flatMap(customer -> {
            System.out.println("Data received by fnf kafka : " + customer);
            //kafkaServerService.send(customer);
            reactiveKafkaServerService.send(customer);
            return Mono.empty();
        }).subscribe();
    }

    @MessageMapping("send.customer")
    public Mono<String> receiveAndSave(@Payload Mono<Customer> customerMono, @Headers Map<String, Object> metadata) {
        return customerMono.flatMap(customer -> {
            System.out.println("Data received by send : " + customer);
            return dbServerService.send(customer);
        }).flatMap(Mono::just);
    }

    @MessageMapping("connector")
    public Mono<Boolean> connector() {
        return Mono.just(Boolean.TRUE);
    }

    @ConnectMapping
    public void connect() {
        //throw new RuntimeException();
        System.out.println("Client Connected");
    }
}