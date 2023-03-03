package com.raj.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RandRResponse<S> {
    private S data;
    private String error; //make as custom class if required
}
