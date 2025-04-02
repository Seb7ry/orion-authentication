package com.unibague.gradework.orionserver;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

//@EnableDiscoveryClient
@SpringBootApplication
public class OrionAuthenticationApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrionAuthenticationApplication.class, args);
    }

}
