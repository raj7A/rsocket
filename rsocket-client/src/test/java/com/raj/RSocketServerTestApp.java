package com.raj;

import com.raj.base.Promise;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import static com.raj.properties.RSocketConstants.STARTUP_VERIFIER_ROUTE;

@SpringBootApplication
public class RSocketServerTestApp {

    @Controller
    @Slf4j
    public static class RSocketServerTestController {

        @MessageMapping("test.rnr")
        private String requestResponseHandler(String data) {
            log.info("RSocket test server - data received on route {} is {}", "test.rnr", data);
            return "received";
        }

        @MessageMapping("test.rnr.promise")
        private Promise requestResponseHandler(Promise data) {
            log.info("RSocket test server - data received on route {} is {}", "test.rnr.promise", data);
            return data;
        }

        @MessageMapping("test.fnf")
        private void fireAndForgetHandler(String data) {
            log.info("RSocket test server - data received on route {} is {}", "test.fnf", data);
        }

        @MessageMapping(STARTUP_VERIFIER_ROUTE)
        private Boolean startUpHandler() {
            log.info("RSocket test server - connected on startUp");
            return Boolean.TRUE;
        }

//        @ConnectMapping()
//        private void connect() {
//            log.info("RSocket test server - connected");
//        }
    }
}