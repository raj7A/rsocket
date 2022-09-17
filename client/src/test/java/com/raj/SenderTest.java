package com.raj;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;

@SpringBootTest
class SenderTest {

    @Autowired
    Sender sender;

    private static final Customer customer = new Customer("1", "raj", "raj");
    static {
        customer.getAddresses().add(new Customer.Address("1"));
        customer.getAddresses().add(new Customer.Address("2"));
    }

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
            var customer = new Customer("1" + a, "raj", "raj");
            //Assertions.assertDoesNotThrow(() -> StepVerifier.create(sender.fireAndForget("vz.customer", customer)).verifyComplete());
            sender.fireAndForget("vz.customer", customer).subscribeOn(Schedulers.single()).subscribe();
            System.out.println("data sent : " + customer);
            return Mono.empty();
        }).blockLast();
    }
    @Test
    void fireAndForgetTheCustomerDataContinuouslyOnDifferentConnections() {
        Flux.interval(Duration.ofMillis(5000)).map(a -> {
            var customer = new Customer("1" + a, "raj", "raj");
            sender.fireAndForgetOnConnection1("vz.customer", customer).subscribeOn(Schedulers.single()).subscribe();
            sender.fireAndForgetOnConnection2("vz.customer", customer).subscribeOn(Schedulers.single()).subscribe();
            System.out.println("data sent : " + customer);
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