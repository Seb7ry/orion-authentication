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

@Service
public class UserService implements IUserService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String USER_SERVICE_URL = "http://localhost:8092/service/user";

    @Override
    public UserLogDTO getUserByEmail(String email) {
        try {
            ResponseEntity<String> response = restTemplate
                    .getForEntity(USER_SERVICE_URL + "/auth/email/" + email, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String json = response.getBody();
                JsonNode root = objectMapper.readTree(json);

                if (root.has("studentID")) {
                    return objectMapper.readValue(json, StudentLogDTO.class);
                } else if (root.has("position")) {
                    return objectMapper.readValue(json, ActorLogDTO.class);
                } else {
                    return objectMapper.readValue(json, UserLogDTO.class);
                }
            }

            throw new RuntimeException("Respuesta vacía al buscar el usuario.");
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("Usuario no encontrado con el correo: " + email);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener el usuario: " + e.getMessage());
        }
    }
}
