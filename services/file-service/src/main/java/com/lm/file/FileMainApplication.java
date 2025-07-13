package com.lm.file;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class FileMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileMainApplication.class, args);
    }
}