package com.raj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class Client {
    public static void main(String[] args) {
        SpringApplication.run(Client.class, args);
    }

//    @RestController
//    public class Controller {
//        @GetMapping("/get")
//        public void get() {
//
//        }
//    }

}