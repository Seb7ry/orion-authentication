package com.unibague.gradework.orionserver.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unibague.gradework.orionserver.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper; // Usa el ObjectMapper configurado en JacksonConfig

    private static final String USER_SERVICE_URL = "http://localhost:8090/service/user";

    @Override
    public Optional<User> fetchUserByEmail(String email) {
        try {
            System.out.println("Fetching user details for email: " + email);

            ResponseEntity<String> response = restTemplate.getForEntity(USER_SERVICE_URL + "/auth/email/" + email, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String responseBody = response.getBody();
                System.out.println("Response from user service: " + responseBody);

                JsonNode jsonNode = objectMapper.readTree(responseBody); // Usa el ObjectMapper corregido

                if (!jsonNode.has("role")) {
                    System.out.println("Error: No role found in response");
                    return Optional.empty();
                }

                String role = jsonNode.get("role").get("name").asText();

                if ("STUDENT".equalsIgnoreCase(role)) {
                    return Optional.of(objectMapper.readValue(responseBody, Student.class));
                } else if ("ACTOR".equalsIgnoreCase(role)) {
                    return Optional.of(objectMapper.readValue(responseBody, Actor.class));
                } else {
                    System.out.println("Unrecognized role: " + role);
                }
            }
        } catch (HttpClientErrorException.NotFound e) {
            System.out.println("User not found for email: " + email);
        } catch (Exception e) {
            System.out.println("Error fetching user details for email " + email + ": " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<ProgramDTO> fetchUserPrograms(String userId) {
        try {
            ResponseEntity<List> response = restTemplate.getForEntity(USER_SERVICE_URL + "/" + userId + "/programs", List.class);
            return response.getBody();
        } catch (Exception e) {
            System.out.println("Error fetching programs for user ID " + userId + ": " + e.getMessage());
        }
        return List.of();
    }
}
