package com.lm.promotion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@EnableDiscoveryClient
@SpringBootApplication
public class PromotionMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(PromotionMainApplication.class, args);
    }
}