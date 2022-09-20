package com.raj;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;

@Service
public class ServerService {

    private static WebClient webClient;

    static {
        webClient = WebClient.create();
//        ConnectionProvider connectionProvider = ConnectionProvider.builder("myConnectionPool")
//                .maxConnections(10000)
//                .pendingAcquireMaxCount(5000)
//                .build();
//        ReactorClientHttpConnector clientHttpConnector = new ReactorClientHttpConnector(HttpClient.create(connectionProvider).keepAlive(true));
//        webClient = WebClient.builder()
//                .clientConnector(clientHttpConnector)
//                .build();
    }

    public Mono<String> send(Customer customer) {
        return webClient.post().uri(URI.create("localhost:3000/content/param1=xyz"))
                .retrieve().bodyToMono(String.class).onErrorReturn("error");
    }
}