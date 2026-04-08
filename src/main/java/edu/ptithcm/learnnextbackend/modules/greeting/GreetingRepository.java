package edu.ptithcm.learnnextbackend.modules.greeting;

import edu.ptithcm.learnnextbackend.modules.greeting.entity.Greeting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GreetingRepository extends JpaRepository<Greeting, Long> {
}
