package com.unibague.gradework.orionauth.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class ApiService implements IApiService {

    private static final Logger log = LoggerFactory.getLogger(ApiService.class);

    @Value("${api.student.url}")
    private String studentApiUrl;

    @Value("${api.actor.url}")
    private String actorApiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ApiService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<JsonNode> findStudentByEmail(String email) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(studentApiUrl, String.class);
            JsonNode array = objectMapper.readTree(response.getBody());

            return StreamSupport.stream(array.spliterator(), false)
                    .filter(node -> email.equalsIgnoreCase(node.path("email").asText()))
                    .findFirst();
        } catch (Exception e) {
            log.error("Error al consultar estudiantes desde API externa: ", e.getMessage(), e);
            throw new RuntimeException("Error al consultar estudiantes desde API externa: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<JsonNode> findActorByEmail(String email) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(actorApiUrl, String.class);
            JsonNode array = objectMapper.readTree(response.getBody());

            return StreamSupport.stream(array.spliterator(), false)
                    .filter(node -> email.equalsIgnoreCase(node.path("email").asText()))
                    .findFirst();
        } catch (Exception e) {
            log.error("Error al consultar actores desde API externa: ", e.getMessage(), e);
            throw new RuntimeException("Error al consultar actores desde API externa: " + e.getMessage(), e);
        }
    }
}