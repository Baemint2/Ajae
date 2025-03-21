package com.ajae.uhtm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class UhtmApplication {

    public static void main(String[] args) {
        SpringApplication.run(UhtmApplication.class, args);
    }

}
