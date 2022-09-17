package com.raj;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class Customer {
    private final String customerId;
    private final String firstName;
    private final String lastName;
    private final List<Address> addresses = new ArrayList<>();

    @Data
    @AllArgsConstructor
    public static class Address {
        private final String id;
    }
}
