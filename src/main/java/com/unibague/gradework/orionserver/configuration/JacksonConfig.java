package com.unibague.gradework.orionserver.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for customizing Jackson serialization.
 * This class ensures that Java 8 date/time API (LocalDate, LocalDateTime, etc.)
 * is properly handled by Jackson.
 */
@Configuration
public class JacksonConfig {

    /**
     * Configures and provides a customized {@link ObjectMapper} bean.
     * This ObjectMapper is registered with the {@link JavaTimeModule} to
     * enable support for Java 8 date and time API.
     *
     * @return a configured {@link ObjectMapper} instance.
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}
