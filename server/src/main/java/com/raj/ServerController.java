package com.raj;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
public class ServerController {

    private final ServerService serverService;
    public ServerController(ServerService serverService) {
        this.serverService = serverService;
    }

    @MessageMapping("fnf.customer")
    public void receive(Mono<Customer> customerMono) {
        customerMono.flatMap(customer -> {
            System.out.println("Data received by fnf : " + customer);
            return serverService.send(customer);
        }).subscribe();
    }

    @MessageMapping("send.customer")
    public Mono<String> receiveAndSave(Mono<Customer> customerMono) {
        return customerMono.flatMap(customer -> {
            System.out.println("Data received by send : " + customer);
            return serverService.send(customer);
        });
    }
}