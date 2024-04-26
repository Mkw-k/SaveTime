package com.save.savetime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "userAuditorAware")
public class SaveTimeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaveTimeApplication.class, args);
    }

}
