package com.unibague.gradework.orionserver.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for defining application-wide beans.
 * Provides a {@link RestTemplate} bean for making HTTP requests.
 */
@Configuration
public class RestTemplateConfig {

    /**
     * Creates and registers a {@link RestTemplate} bean.
     *
     * @return a new instance of {@link RestTemplate}.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
