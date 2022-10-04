package com.raj;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class Customer {
    private  Integer customerId;
    private  String firstName;
    private  String lastName;
    private  List<Address> addresses;

    public Customer(Integer customerId, String firstName, String lastName, List<Address> addresses) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.addresses = addresses;
    }

    public Customer() {
    }

    @Data
    //@AllArgsConstructor
    //@NoArgsConstructor
    static class Address {
        private  String id;

        @JsonCreator
        public Address(String id) {
            this.id = id;
        }
    }
}
