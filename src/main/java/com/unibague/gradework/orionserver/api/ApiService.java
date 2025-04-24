package com.unibague.gradework.orionserver.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class ApiService implements IApiService {

    @Value("${STUDENT_API_URL}")
    private String studentApiUrl;

    @Value("${ACTOR_API_URL}")
    private String actorApiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
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
            throw new RuntimeException("Error al consultar estudiantes desde API externa: " + e.getMessage());
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
            throw new RuntimeException("Error al consultar actores desde API externa: " + e.getMessage());
        }
    }
}
