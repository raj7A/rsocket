package com.raj;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
public class ServerController {

    @MessageMapping("vz")
    public void receive(Mono<String> data) {
        data.map(value -> {
            System.out.println(value);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return Mono.empty();
        }).subscribe();
    }

    @MessageMapping("vz.customer")
    public void receiveCustomer(Mono<Customer> data) {
        data.map(value -> {
            System.out.println(value.toString());
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return Mono.empty();
        }).subscribe();
    }
}