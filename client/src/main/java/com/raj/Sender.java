package com.raj;

import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class Sender {

    RSocketRequester rSocketRequester1;
    RSocketRequester rSocketRequester2;

    private Sender(RSocketRequester rSocketRequester1, RSocketRequester rSocketRequester2) {
        this.rSocketRequester1 = rSocketRequester1;
        this.rSocketRequester2 = rSocketRequester2;
    }

    public Mono<Void> fireAndForget(String route, Customer message) {
        return rSocketRequester1.route(route)
                .data(getMessage(message))
                .send();
    }

    private Customer getMessage(Customer message) {
        return message;
    }

    public Mono<Void> fireAndForgetOnConnection1(String route, Customer message) {
        return rSocketRequester1.route(route)
                .data(message)
                .send();
    }

    public Mono<Void> fireAndForgetOnConnection2(String route, Customer message) {
        return rSocketRequester2.route(route)
                .data(message)
                .send();
    }

    public Mono<String> send(String route, Customer customer) {
        return rSocketRequester1.route(route)
                .data(customer)
                .retrieveMono(String.class)
                .map(response -> {
                    System.out.println(response);
                    return response;
                });
    }
}
