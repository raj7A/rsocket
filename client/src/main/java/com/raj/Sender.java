package com.raj;

import io.rsocket.exceptions.ApplicationErrorException;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;
import reactor.core.Disposable;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.rmi.ConnectIOException;

@Component
public class Sender {

    RSocketRequester rSocketRequester1;
    RSocketRequester rSocketRequester2;
    private static Scheduler scheduler = Schedulers.newSingle("My-Thread", false);
    private boolean connected;
    private Sender(RSocketRequester rSocketRequester1, RSocketRequester rSocketRequester2) {
        this.rSocketRequester1 = rSocketRequester1;
        this.rSocketRequester2 = rSocketRequester2;
        connect();
    }

    public Disposable fireAndForgetSubscribed(String route, Customer customer) {
        return Mono.just(customer)
                .map(cust -> fireAndForget(route, cust).subscribe())
                .subscribeOn(scheduler)
                .subscribe();
    }

    public Mono<Void> fireAndForget(String route, Customer message) {
        return rSocketRequester1.route(route)
                .metadata(new Trace().toString(), MimeType.valueOf("message/x.rsocket.composite-metadata.v0"))
                .data(getMessage(message))
                .send();
    }

    private Object getMessage(Customer message) {
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

    private void connect() {
        rSocketRequester1.route("connector")
                .retrieveMono(Boolean.class)
                .onErrorReturn(ApplicationErrorException.class, true)
                .onErrorReturn(false)
                .map(result -> connected = result)
                .subscribeOn(scheduler)
                .subscribe();
    }

    public boolean isConnected() {
        return connected;
    }

}
