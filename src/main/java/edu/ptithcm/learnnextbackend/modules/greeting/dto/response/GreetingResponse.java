package edu.ptithcm.learnnextbackend.modules.greeting.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GreetingResponse {

    private  Long id;
    private String message;
}