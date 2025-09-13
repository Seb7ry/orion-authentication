package com.unibague.gradework.orionauth.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Configuration
public class RestTemplateConfig {

    @Value("${gateway.service.token:auth-service-token-secure-2025}")
    private String serviceToken;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // Add interceptor para comunicación service-to-service através del Gateway
        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
        interceptors.add((request, body, execution) -> {
            String url = request.getURI().toString();

            // Solo agregar headers de servicio interno si va al Gateway (puerto 8080)
            if (url.contains(":8080/api/")) {
                log.debug("Adding service headers for gateway communication: {}", url);

                // Headers para identificar comunicación service-to-service
                request.getHeaders().add("X-Service-Request", "true");
                request.getHeaders().add("X-Service-Name", "orion-auth");
                request.getHeaders().add("X-Service-Token", serviceToken);
                request.getHeaders().add("User-Agent", "orion-auth-service/1.0.0");
                request.getHeaders().add("X-Internal-Request", "true");

            } else {
                log.debug("External API call, not adding service headers: {}", url);
            }

            return execution.execute(request, body);
        });

        restTemplate.setInterceptors(interceptors);
        log.info("RestTemplate configured for gateway communication with token: {}",
                serviceToken != null ? serviceToken.substring(0, 10) + "..." : "null");
        return restTemplate;
    }
}