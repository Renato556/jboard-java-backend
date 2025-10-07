package br.com.jboard.orchestrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class JboardJavaOrchestratorApplication {
    public static void main(String[] args) {
        SpringApplication.run(JboardJavaOrchestratorApplication.class, args);
    }
}
