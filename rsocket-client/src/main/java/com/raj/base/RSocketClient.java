package com.raj.base;

import com.raj.validator.RSocketValidator;
import com.raj.properties.RSocketConstants;
import com.raj.properties.RSocketProperties;
import com.raj.response.RandRResponse;
import io.rsocket.exceptions.ApplicationErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class RSocketClient<T> {

    private final RSocketRequester rSocketRequester;
    private final RSocketProperties rSocketProperties;
    private final Scheduler scheduler = Schedulers.newSingle("Ofs-RSocket-Client-Thread", true);
    private final AtomicBoolean connectedOnStartUp = new AtomicBoolean(false);

    public RSocketClient(RSocketRequester rSocketRequester, RSocketProperties rSocketProperties) {
        this.rSocketRequester = rSocketRequester;
        this.rSocketProperties = rSocketProperties;
        connectToServerOnDefaultRoute();
        log.debug("Configured RSocket properties :: {}", rSocketProperties.toString());
    }

    public Mono<Boolean> fireAndForgetWithSubscription(String route, T data) {
        var isProcessed = RSocketValidator.isGoodToProcess(route, data, rSocketProperties.isRSocketEnabled());
        Mono.just(RSocketConstants.BLANK)
                .filter(empty -> isProcessed == Boolean.TRUE)
                .map(empty -> fireAndForget(route, data).subscribe())
                .subscribeOn(scheduler)
                .subscribe();
        return Mono.just(isProcessed);
    }

    public Mono<Boolean> fireAndForgetWithSubscription(T data) {
        return fireAndForgetWithSubscription(rSocketProperties.getRoute(), data);
    }

    protected Mono<Void> fireAndForget(String route, T data) {
        log.debug("Sending data asynchronously to server route {}", route);
        return rSocketRequester.route(route)
                .data(data)
                .send();
    }

    private <S> Mono<S> requestResponse(String route, T data, Class<S> responseType) {
        return rSocketRequester.route(route)
                .data(data)
                .retrieveMono(responseType);
    }

    protected <S> Mono<RandRResponse<S>> requestAndResponse(String route, T data, Class<S> responseType) {
        RandRResponse<S> response = new RandRResponse<>();
        if (!RSocketValidator.isGoodToProcess(route, data, rSocketProperties.isRSocketEnabled())) {
            response.setError(RSocketConstants.ERROR);
            return Mono.just(response);
        }
        return requestResponse(route, data, responseType)
                .onErrorResume(throwable -> {
                    response.setError(throwable.getMessage());
                    try {
                        return Mono.just(responseType.getDeclaredConstructor().newInstance());
                    } catch (Exception e) {
                        return Mono.empty();
                    }
                })
                .flatMap(resp -> {
                    response.setData(resp);
                    return Mono.just(response);
                });
    }

    private void connectToServerOnDefaultRoute() {
        if (rSocketProperties.isRSocketEnabled() && rSocketProperties.isConnectOnStart())
            requestResponse(RSocketConstants.STARTUP_VERIFIER_ROUTE, (T) RSocketConstants.BLANK, Boolean.class)
                    .onErrorReturn(ApplicationErrorException.class, Boolean.TRUE)
                    .onErrorReturn(Boolean.FALSE)
                    .map(result -> {
                        log.info("RSocket client {} to server {}:{} on-start up", result ? "connected" : "not connected", rSocketProperties.getHost(), rSocketProperties.getPort());
                        return connectedOnStartUp.getAndSet(result);
                    })
                    .subscribeOn(scheduler)
                    .subscribe();
    }

    public boolean isConnectedOnStartUp() {
        return connectedOnStartUp.get();
    }
}
