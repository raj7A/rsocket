package com.raj.base;

import com.raj.properties.RSocketConstants;
import com.raj.response.RandRResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import reactor.test.StepVerifier;

@SpringBootTest
@TestPropertySource(properties = "spring.rsocket.server.port=9090")
class RSocketClientTest {

    @Autowired
    RSocketClient rSocketClient;

    @Test
    void connectOnStartUp() {
        Assertions.assertTrue(rSocketClient.isConnectedOnStartUp());
    }

    @Test
    void fireAndForgetTheData() {
        //await().pollDelay(2, TimeUnit.SECONDS).until(() -> true);
        StepVerifier.create(rSocketClient.fireAndForgetWithSubscription("test.fnf", "test data"))
                .expectNext(Boolean.TRUE).verifyComplete();
    }

    @Test
    void fireAndForgetTheDataOnDefaultConfiguredRoute() {
        StepVerifier.create(rSocketClient.fireAndForgetWithSubscription("test data"))
                .expectNext(Boolean.TRUE).verifyComplete();
    }

    @Test
    void fireAndForgetErrorsWhenRouteIsNull() {
        StepVerifier.create(rSocketClient.fireAndForgetWithSubscription(null, "test data"))
                .expectNext(Boolean.FALSE).verifyComplete();
    }

    @Test
    void fireAndForgetErrorsWhenDataIsNull() {
        StepVerifier.create(rSocketClient.fireAndForgetWithSubscription("test.fnf", null))
                .expectNext(Boolean.FALSE).verifyComplete();
    }

    @Test
    void sendRequestAndReceiveResponse() {
        StepVerifier.create(rSocketClient.requestAndResponse("test.rnr", "test data", String.class))
                .expectNext(new RandRResponse<>("received", null)).verifyComplete();
    }

    @Test
    void sendRequestAndReceiveResponseOnNonConfiguredRouteOnServer() throws Exception {
        StepVerifier.create(rSocketClient.requestAndResponse("route.not.configured", "test data", String.class))
                .expectNext(new RandRResponse<>(String.class.getDeclaredConstructor().newInstance(), "No handler for destination 'route.not.configured'")).verifyComplete();
    }

    @Test
    void sendRequestAndReceiveResponseOfPromiseType() {
        StepVerifier.create(rSocketClient.requestAndResponse("test.rnr.promise", new Promise(1), Promise.class))
                .expectNext(new RandRResponse<>(new Promise(1), null)).verifyComplete();
    }

    @Test
    void sendRequestAndReceiveErrorResponseWithRouteAsNull() {
        StepVerifier.create(rSocketClient.requestAndResponse(null, "test data", String.class))
                .expectNext(new RandRResponse<>(null, RSocketConstants.ERROR)).verifyComplete();
    }

    @Test
    void sendRequestAndReceiveErrorResponseWithDataAsNull() {
        StepVerifier.create(rSocketClient.requestAndResponse("test.fnf", null, String.class))
                .expectNext(new RandRResponse<>(null, RSocketConstants.ERROR)).verifyComplete();
    }

    @TestPropertySource(properties = "spring.rsocket.server.port=0000")
    @SpringBootTest
    @Nested
    static class RSocketClientBadServerTest {
        @Autowired
        RSocketClient<String> rSocketClient;
        @Test
        void fireAndForgetTheDataWhenServerIsDown() {
            StepVerifier.create(rSocketClient.fireAndForget("test.fnf", "test data")).verifyError(Exception.class);
        }
    }
}