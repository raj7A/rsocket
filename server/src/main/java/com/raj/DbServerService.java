package com.raj;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.net.URI;
import java.time.Duration;

import static io.netty.channel.ChannelOption.SO_KEEPALIVE;

@Service
public class DbServerService {

    private static final WebClient webClient;

    static {
//      webClient = WebClient.create();
        ConnectionProvider connectionProvider = ConnectionProvider.builder("myConnectionPool")
                .maxConnections(500)
                .maxIdleTime(Duration.ofMillis(120000))
                .maxLifeTime(Duration.ofMillis(130000))
                .pendingAcquireMaxCount(1000)
                .build();
        HttpClient client =  HttpClient.create(connectionProvider)
                .option(SO_KEEPALIVE, Boolean.TRUE);
//      TcpClient tcp = TcpClient.create()
//                    .option(EpollChannelOption.TCP_KEEPIDLE, 1)
//                .option(SO_KEEPALIVE, Boolean.TRUE)
//                .option(EpollChannelOption.TCP_KEEPCNT, 10)
//                .option(EpollChannelOption.TCP_KEEPINTVL, 2);
//      HttpClient client =  HttpClient.from(tcp);
//                .option(SO_LINGER, 10000000)
//                .option(EpollChannelOption.TCP_KEEPIDLE, 1)
//                .option(EpollChannelOption.TCP_KEEPCNT, 10)
//                .option(EpollChannelOption.TCP_KEEPINTVL, 2);
        webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(client))
                .build();
    }

    public Mono<String> send(Customer customer) {
        //return webClient.post().uri(URI.create("localhost:3000/content/param1=xyz"))
        return webClient.get().uri(URI.create("localhost:8080/v1/departments/D102/projects/P1001/employees"))
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