package com.lm.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;




@EnableDiscoveryClient
@SpringBootApplication
public class CartMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(CartMainApplication.class, args);
    }
}