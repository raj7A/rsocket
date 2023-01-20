package com.raj;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.client.Http2AllocationStrategy;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.net.URI;

import static io.netty.channel.ChannelOption.SO_KEEPALIVE;

@Service
public class DbServerService {

    private static final WebClient webClient;
    private static final WebClient anotherWebClient;

    static {
        webClient = http1dot1();
        anotherWebClient = http1dot1();
        //webClient = http2(); // make sure the server is http2 enabled
    }

    private static WebClient http2() {
        HttpClient client = HttpClient.create(ConnectionProvider.builder("test")
                .maxConnections(50)
                .allocationStrategy(Http2AllocationStrategy.builder().maxConnections(50).build())
                .build()).protocol(HttpProtocol.H2C);
        return WebClient.create().mutate()
                .clientConnector(new ReactorClientHttpConnector(client)).build();
    }

    private static WebClient http1dot1() {
        ConnectionProvider connectionProvider = ConnectionProvider.builder("myConnectionPool")
                .maxConnections(10)
                //.maxIdleTime(Duration.ofMillis(120000))
                //.maxLifeTime(Duration.ofMillis(130000))
                .pendingAcquireMaxCount(20)
                .build();
        HttpClient client = HttpClient.create(connectionProvider)
                .option(SO_KEEPALIVE, Boolean.TRUE);

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(client))
                .build();
    }

    public Mono<String> send(Customer customer) {
        return webClient.post().uri(URI.create("localhost:3000/content/param1=xyz"))
        //return webClient.get().uri(URI.create("localhost:8080/v1/departments/D102/projects/P1001/employees"))
                //return webClient.get().uri(URI.create("employee-project-service-service:8080/v1/departments/D102/projects/P1001/employees"))
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

    public Mono<String> sendAnother(String customer) {
        return anotherWebClient.post().uri(URI.create("localhost:3000/users"))
        //return webClient.get().uri(URI.create("localhost:8080/v1/departments/D102/projects/P1001/employees"))
                //return webClient.get().uri(URI.create("employee-project-service-service:8080/v1/departments/D102/projects/P1001/employees"))
                .retrieve().bodyToMono(String.class).onErrorResume(error -> {
                    System.err.println(error.toString());
                    return Mono.just("not sent : " + customer);
                })
                .flatMap(response -> {
                    if (response.equalsIgnoreCase("not sent")) {
                        System.out.println(response);
                        return Mono.just(response);
                    }
                    System.out.println("Data sent to save : " + customer);
                    return Mono.just("sent");
                });
    }

}