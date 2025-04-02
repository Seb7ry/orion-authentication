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
    private ObjectMapper objectMapper;

    private static final String USER_SERVICE_URL = "http://localhost:8092/service/user";

    @Override
    public User getUserByEmail(String email) {
        try {
            ResponseEntity<String> response = restTemplate
                    .getForEntity(USER_SERVICE_URL + "/auth/email/" + email, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String responseBody = response.getBody();
                System.out.println("Response from user service: " + responseBody);

                JsonNode jsonNode = objectMapper.readTree(responseBody);

                if (!jsonNode.has("role")) {
                    throw new RuntimeException("No se encontró el rol en la respuesta del usuario.");
                }

                String role = jsonNode.get("role").get("name").asText();

                if ("STUDENT".equalsIgnoreCase(role)) {
                    return objectMapper.readValue(responseBody, Student.class);
                } else if ("ACTOR".equalsIgnoreCase(role)) {
                    return objectMapper.readValue(responseBody, Actor.class);
                } else {
                    System.out.println("Unrecognized role: " + role);
                }
            }
            throw new RuntimeException("Respuesta vacía o incorrecta al buscar el usuario.");
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("Usuario no encontrado con el correo: " + email);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener el usuario: " + e.getMessage());
        }
    }
}
