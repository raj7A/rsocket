package com.raj;

import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class Sender {

    RSocketRequester rSocketRequester;

    private Sender(RSocketRequester rSocketRequester) {
        this.rSocketRequester = rSocketRequester;
    }

    public Mono<Void> fireAndForget(String route, Customer message) {
        return rSocketRequester.route(route)
                .data(message)
                .send();
    }

    public Mono<String> send(String route, String message) {
        return rSocketRequester.route(route)
                .data(message)
                .retrieveMono(String.class);
    }
}
