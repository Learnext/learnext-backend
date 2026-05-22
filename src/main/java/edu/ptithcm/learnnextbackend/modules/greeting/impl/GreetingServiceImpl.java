package edu.ptithcm.learnnextbackend.modules.greeting.impl;

import edu.ptithcm.learnnextbackend.modules.greeting.GreetingRepository;
import edu.ptithcm.learnnextbackend.modules.greeting.GreetingService;
import edu.ptithcm.learnnextbackend.modules.greeting.dto.response.GreetingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GreetingServiceImpl implements GreetingService {
    @Autowired
    private GreetingRepository greetingRepository;

    @Override
    public GreetingResponse hello() {
        return greetingRepository.findById(1L)
                .map(greeting -> GreetingResponse.builder()
                        .id(greeting.getId())
                        .message(greeting.getMessage())
                        .build())
                .orElseGet(() -> GreetingResponse.builder()
                        .id(0L)
                        .message("Hello from LearnNext")
                        .build());
    }
}
