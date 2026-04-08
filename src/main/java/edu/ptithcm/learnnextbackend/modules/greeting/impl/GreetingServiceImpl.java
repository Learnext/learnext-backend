package edu.ptithcm.learnnextbackend.modules.greeting.impl;

import edu.ptithcm.learnnextbackend.modules.greeting.GreetingRepository;
import edu.ptithcm.learnnextbackend.modules.greeting.GreetingService;
import edu.ptithcm.learnnextbackend.modules.greeting.dto.response.GreetingResponse;
import edu.ptithcm.learnnextbackend.modules.greeting.entity.Greeting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GreetingServiceImpl implements GreetingService {
    @Autowired
    private GreetingRepository greetingRepository;

    @Override
    public GreetingResponse hello() {
        Greeting greeting = greetingRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Not found"));
        return GreetingResponse.builder()
                .id(greeting.getId())
                .message(greeting.getMessage())
                .build();
    }
}
