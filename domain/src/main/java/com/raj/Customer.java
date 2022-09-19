package com.raj;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Customer {
    private final Integer customerId;
    private final String firstName;
    private final String lastName;
    private final List<Address> addresses;

    @Data
    //@AllArgsConstructor
    //@NoArgsConstructor
    static class Address {
        private final String id;

        @JsonCreator
        public Address(String id) {
            this.id = id;
        }
    }
}
