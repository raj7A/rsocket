package com.raj;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;

@Service
public class DbServerService {

    private static final WebClient webClient = WebClient.create();

    public Mono<String> send(Customer customer) {
        return webClient.post().uri(URI.create("localhost:3000/content/param1=xyz"))
                .retrieve().bodyToMono(String.class).onErrorResume(error -> {
                    System.err.println(error.toString());
                    return Mono.just("not sent : " + customer.getCustomerId());
                })
                .flatMap(response -> {
                    if (response.equalsIgnoreCase("not sent")) {
                        System.out.println(response);
                        return Mono.just(response);
                    }
                    System.out.println("Data sent to save : " + customer.getCustomerId());
                    return Mono.just("sent");
                });
    }

}