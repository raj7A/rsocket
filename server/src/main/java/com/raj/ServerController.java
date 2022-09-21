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

    private final ServerService serverService;
    public ServerController(ServerService serverService) {
        this.serverService = serverService;
    }

    @MessageMapping("fnf.customer")
    public void receive(@Payload Mono<Customer> customerMono, @Headers Map<String, Object> metadata) {
        customerMono.flatMap(customer -> {
            System.out.println("Data received by fnf : " + customer);
            return serverService.send(customer);
        }).subscribe();
    }

    @MessageMapping("send.customer")
    public Mono<String> receiveAndSave(@Payload Mono<Customer> customerMono, @Headers Map<String, Object> metadata) {
        return customerMono.flatMap(customer -> {
            System.out.println("Data received by send : " + customer);
            return serverService.send(customer);
        }).flatMap(Mono::just);
    }

    @ConnectMapping
    public void connect() {
        //throw new RuntimeException();
        System.out.println("Client Connected");
    }
}