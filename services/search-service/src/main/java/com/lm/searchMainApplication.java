package com.lm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@EnableDiscoveryClient
@SpringBootApplication
public class searchMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(searchMainApplication.class, args);
    }
}