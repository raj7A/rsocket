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
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
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

    @RepeatedTest(1)
    void fireAndForgetTheCustomerData() {
        Assertions.assertDoesNotThrow(() ->
                StepVerifier.create(sender.fireAndForget("fnf.k.customer", customer)).verifyComplete());
    }

    @RepeatedTest(1)
    void sendTheCustomerData() {
        Assertions.assertDoesNotThrow(() -> StepVerifier.create(sender.send("send.customer", customer)).expectNext("sent")
                .verifyComplete());
    }

    @Test
    void fireAndForgetTheCustomerDataContinuously() throws InterruptedException {
        CountDownLatch count = new CountDownLatch(1);

        IntStream.range(1, 20).boxed()
                .forEach(counter -> {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    var customer = new Customer(counter, "raj", "raj", addresses);
                    //Assertions.assertDoesNotThrow(() -> StepVerifier.create(sender.fireAndForget("fnf.k.customer", customer)).verifyComplete());
                    sender.fireAndForgetSubscribed("fnf.k.customer", customer);
                    System.out.println("Data sent to server : " + customer);
                });

        count.await();
    }

    @Test
    void fireAndForgetTheCustomerDataContinuouslyInParallelMode() throws InterruptedException {
        CountDownLatch count = new CountDownLatch(1);

        Flux.range(1, 50)
                .parallel(4)
                .runOn(Schedulers.parallel())
                .map(counter -> {
                    var customer = new Customer(counter, "raj", "raj", addresses);
                    sender.fireAndForgetSubscribed("fnf.k.customer", customer);
                    System.out.println(Thread.currentThread().getName() + " : Data sent to server : " + customer);
                    return counter;
                }).sequential().blockLast();

        count.await();
    }

    @Test
    void fireAndForgetTheBulkCustomerDataContinuously() throws InterruptedException {
        makeCustomerDataBulk();
        CountDownLatch count = new CountDownLatch(1);

        Flux.range(1, 100000)
                .delayElements(Duration.ofMillis(5))
                .map(counter -> {
                    var customer = new Customer(counter, "raj", "raj", addresses);
                    sender.fireAndForgetSubscribed("fnf.k.customer", customer);
                    System.out.println("Data sent to server : " + customer);
                    return Mono.empty();
                }).blockLast();

        count.await();
    }

    @Test
    void fireAndForgetTheBulkCustomerDataContinuouslyInParallelMode() throws InterruptedException {
        makeCustomerDataBulk();
        CountDownLatch count = new CountDownLatch(1);

        Flux.range(1, 1000)
                .parallel(4)
                .runOn(Schedulers.parallel())
                .map(counter -> {
                    var customer = new Customer(counter, "raj", "raj", addresses);
                    sender.fireAndForgetSubscribed("fnf.k.customer", customer);
                    System.out.println( " Data sent to server : " + customer.getCustomerId());
                    return counter;
                }).sequential().blockLast();

        count.await();
    }

    @Test
    void fireAndForgetTheCustomerDataContinuouslyOnDifferentConnections() {
        AtomicInteger counter = new AtomicInteger();

        Flux.interval(Duration.ofMillis(5000)).map(a -> {
            var customer = new Customer(counter.getAndIncrement(), "raj", "raj", addresses);
            sender.fireAndForgetSubscribed("fnf.k.customer", customer);
            sender.fireAndForgetSubscribed("fnf.k.customer", customer);
            System.out.println("Data sent to server : " + customer);
            return Mono.empty();
        }).blockLast();
    }

    @Test
    void sendTheCustomerDataContinuously() throws InterruptedException {
        CountDownLatch count = new CountDownLatch(1);

        Flux.range(1, 10)
                .delayElements(Duration.ofMillis(10))
                .map(counter -> {
                    var customer = new Customer(counter, "raj", "raj", addresses);
                    sender.send("send.customer", customer).subscribeOn(scheduler).subscribe();
                    System.out.println("Data sent to server : " + customer);
                    return counter;
                }).blockLast();

        count.await();
    }

    @Test
    void keepAliveConnectionAfterFireAndForgetTheCustomerData() {
        Flux.interval(Duration.ofMillis(1000)).map(a -> {
            System.out.println("sending data : " + customer);
            Assertions.assertDoesNotThrow(() -> StepVerifier.create(sender.fireAndForget("fnf.k.customer", customer)).verifyComplete());
            try {
                Thread.sleep(100000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return Mono.empty();
        }).blockLast();
    }

    private void makeCustomerDataBulk() {
        IntStream.rangeClosed(1, 4000).boxed().forEach(value -> {
            // ~ 0.25 MB
            addresses.add(new Customer.Address(UUID.randomUUID().toString()));
        });
    }

}