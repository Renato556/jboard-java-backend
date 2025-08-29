package com.jboard.jboardjavabackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class JboardJavaBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(JboardJavaBackendApplication.class, args);
    }
}
