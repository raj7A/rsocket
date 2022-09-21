package com.raj;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Random;

@Data
public class Trace {
    private final Integer id;

    public Trace() {
        this.id = new Random().nextInt(1, 99999);
    }
}
