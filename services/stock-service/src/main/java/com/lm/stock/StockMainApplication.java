package com.lm.stock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class StockMainApplication {

    public static void main(String[] args) {

        SpringApplication.run(StockMainApplication.class, args);
    }
}

