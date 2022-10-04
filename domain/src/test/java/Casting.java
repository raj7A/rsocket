import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.raj.Customer;

import java.util.ArrayList;

public class Casting {
    @org.junit.jupiter.api.Test
    void name() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        Object customer = new Customer(1, "r", "r", new ArrayList<>());
        String customerJson = mapper.writeValueAsString(customer);
        System.out.println(customerJson);

        Customer customerObject = mapper.readValue(customerJson, Customer.class);
        System.out.println(customerObject.toString());
    }
}