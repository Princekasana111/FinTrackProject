package com.fintrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the FinTrack backend.
 * <p>
 * {@code @SpringBootApplication} bundles three annotations:
 *   - @Configuration      (this class can define beans)
 *   - @EnableAutoConfiguration (Spring Boot wires up sensible defaults)
 *   - @ComponentScan      (finds @Service, @RestController, @Repository, etc.)
 */
@SpringBootApplication
public class FintrackApplication {

    public static void main(String[] args) {
        SpringApplication.run(FintrackApplication.class, args);
    }
}
