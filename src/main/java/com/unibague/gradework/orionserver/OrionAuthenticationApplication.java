package com.unibague.gradework.orionserver;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class OrionAuthenticationApplication {

    static {
        Dotenv dotenv = Dotenv.load();

        System.out.println("🔍 Verificando variables de entorno:");
        System.out.println("GOOGLE_CLIENT_ID: " + dotenv.get("GOOGLE_CLIENT_ID"));
        System.out.println("GOOGLE_CLIENT_SECRET: " + dotenv.get("GOOGLE_CLIENT_SECRET"));
        System.out.println("JWT_SECRET: " + dotenv.get("JWT_SECRET"));

        System.setProperty("GOOGLE_CLIENT_ID", dotenv.get("GOOGLE_CLIENT_ID"));
        System.setProperty("GOOGLE_CLIENT_SECRET", dotenv.get("GOOGLE_CLIENT_SECRET"));
        System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
    }
    public static void main(String[] args) {
        SpringApplication.run(OrionAuthenticationApplication.class, args);
    }

}
