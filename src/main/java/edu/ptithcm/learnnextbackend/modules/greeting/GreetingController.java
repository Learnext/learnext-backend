package edu.ptithcm.learnnextbackend.modules.greeting;

import edu.ptithcm.learnnextbackend.modules.greeting.dto.response.GreetingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/greeting")
public class GreetingController {
    @Autowired
    private GreetingService greetingService;

    @GetMapping({"", "/"})
    public GreetingResponse hello(){
        return greetingService.hello();
    }
}
