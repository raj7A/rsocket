package com.raj;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

@SpringBootTest
class SenderTest {

    @Autowired
    Sender sender;

    private final Customer customer = new Customer("1", "raj");

    @Test
    void fireAndForgetTheCustomerData() {
        Assertions.assertDoesNotThrow(() ->
                StepVerifier.create(sender.fireAndForget("vz.customer", customer)).verifyComplete());
    }

    @Test
    void sendTheData() {
        Assertions.assertDoesNotThrow(() -> StepVerifier.create(sender.send("vz", "rnr data")).verifyComplete());
    }

    @Test
    void fireAndForgetTheCustomerDataContinuously() {
        Flux.interval(Duration.ofMillis(1000)).map(a -> {
            var customer = new Customer("1" + a, "raj");
            System.out.println("sending data : " + customer.toString());
            //Assertions.assertDoesNotThrow(() -> StepVerifier.create(sender.fireAndForget("vz.customer", customer)).verifyComplete());
            sender.fireAndForget("vz.customer", customer).subscribe();
            return Mono.empty();
        }).blockLast();
    }

    @Test
    void keepAliveConnectionAfterFireAndForgetTheCustomerData() {
        Flux.interval(Duration.ofMillis(1000)).map(a -> {
            System.out.println("sending data : " + a);
            Assertions.assertDoesNotThrow(() -> StepVerifier.create(sender.fireAndForget("vz.customer", customer)).verifyComplete());
            try {
                Thread.sleep(1000000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return Mono.empty();
        }).blockLast();
    }

}