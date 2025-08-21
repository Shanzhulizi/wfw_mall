package com.lm.logistic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class LogisticMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(LogisticMainApplication.class, args);
    }

}
