package edu.ptithcm.learnnextbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LearnNextBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearnNextBackendApplication.class, args);
    }

}
