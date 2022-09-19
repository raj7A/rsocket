package com.raj;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@SpringBootTest
class SenderTest {

    @Autowired
    private Sender sender;

    private static Scheduler scheduler = Schedulers.newSingle("My-Thread", false);

    private static Customer customer = null;
    private static final List<Customer.Address> addresses = new ArrayList<>();

    @BeforeAll
    static void beforeAll() {
        addresses.add(new Customer.Address("1"));
        addresses.add(new Customer.Address("2"));
        customer = new Customer(1, "raj", "raj", addresses);
    }

    @RepeatedTest(2)
    void fireAndForgetTheCustomerData() {
        Assertions.assertDoesNotThrow(() ->
                StepVerifier.create(sender.fireAndForget("fnf.customer", customer)).verifyComplete());
    }

    @RepeatedTest(2)
    void sendTheCustomerData() {
        Assertions.assertDoesNotThrow(() -> StepVerifier.create(sender.send("send.customer", customer)).expectNext("saved")
                .verifyComplete());
    }

    @Test
    void fireAndForgetTheCustomerDataContinuouslyOnDifferentConnections() {
        AtomicInteger counter = new AtomicInteger();
        Flux.interval(Duration.ofMillis(5000)).map(a -> {
            var customer = new Customer(counter.getAndIncrement(), "raj", "raj", addresses);
            sender.fireAndForgetOnConnection1("fnf.customer", customer).subscribeOn(Schedulers.single()).subscribe();
            sender.fireAndForgetOnConnection2("fnf.customer", customer).subscribeOn(Schedulers.single()).subscribe();
            System.out.println("Data sent : " + customer);
            return Mono.empty();
        }).blockLast();
    }

    @Test
    void fireAndForgetTheCustomerDataContinuously() {
        var counter = new AtomicInteger();
        Flux.interval(Duration.ofMillis(10))
                .map(interval -> {
                    var customer = new Customer(counter.getAndIncrement(), "raj", "raj", addresses);
                    //Assertions.assertDoesNotThrow(() -> StepVerifier.create(sender.fireAndForget("fnf.customer", customer)).verifyComplete());
                    sender.fireAndForget("fnf.customer", customer).subscribeOn(scheduler).subscribe();
                    System.out.println("Data sent : " + customer);
                    return Mono.empty();
                }).map(empty -> {
                    if (counter.get() == 10) {
                        try {
                            Thread.sleep(1000000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return Mono.empty();
                }).blockLast();
    }

    @Test
    void sendTheCustomerDataContinuously() {
        Flux.fromStream(() -> IntStream.rangeClosed(0, 10).boxed())
                .delayElements(Duration.ofMillis(10))
                .map(counter -> {
                    var customer = new Customer(counter, "raj", "raj", addresses);
                    //Assertions.assertDoesNotThrow(() -> StepVerifier.create(sender.fireAndForget("fnf.customer", customer)).verifyComplete());
                    sender.send("send.customer", customer).subscribeOn(scheduler).subscribe();
                    System.out.println("Data sent : " + customer);
                    return counter;
                }).map(ctr -> {
                    if (ctr == 10) {
                        try {
                            Thread.sleep(1000000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return Mono.empty();
                }).blockLast();
    }

    @Test
    void keepAliveConnectionAfterFireAndForgetTheCustomerData() {
        Flux.interval(Duration.ofMillis(1000)).map(a -> {
            System.out.println("sending data : " + customer);
            Assertions.assertDoesNotThrow(() -> StepVerifier.create(sender.fireAndForget("fnf.customer", customer)).verifyComplete());
            try {
                Thread.sleep(1000000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return Mono.empty();
        }).blockLast();
    }

}