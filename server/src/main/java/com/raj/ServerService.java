package com.raj;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;

@Service
public class ServerService {

    private static WebClient webClient = WebClient.create();

    public Mono<String> send(Customer customer) {
        return webClient.post().uri(URI.create("localhost:3000/content/param1=xyz"))
                .retrieve().bodyToMono(String.class).doOnNext(resp -> System.out.println("posted")).thenReturn("saved");
    }
}