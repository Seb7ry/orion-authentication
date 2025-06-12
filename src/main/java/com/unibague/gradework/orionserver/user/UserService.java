package com.unibague.gradework.orionserver.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unibague.gradework.orionserver.user.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class UserService implements IUserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${user.service.url}")
    private String userServiceUrl;

    @Value("${role.service.url}")
    private String roleServiceUrl;

    @Override
    public UserLogDTO getUserByEmail(String email) {
        try {
            ResponseEntity<String> response = restTemplate
                    .getForEntity(userServiceUrl + "/auth/email/" + email, String.class);

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

            log.warn("Respuesta vacía al buscar el usuario con email: ", email);
            throw new RuntimeException("Respuesta vacía al buscar el usuario.");
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Usuario no encontrado con el correo: ", email);
            throw new RuntimeException("Usuario no encontrado.");
        } catch (Exception e) {
            log.error("Error al obtener el usuario con email: ", email, e.getMessage(), e);
            throw new RuntimeException("Error al obtener el usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public void createStudent(Student student) {
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    userServiceUrl + "/student",
                    student,
                    String.class
            );

            log.info("JSON enviado al microservicio USER: ", objectMapper.writeValueAsString(student));

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Error al crear estudiante. Código: " + response.getStatusCode());
            }

            log.info("Estudiante creado exitosamente: ", student.getEmail());

        } catch (Exception e) {
            log.error("Error al crear estudiante: ", student.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Error al crear estudiante: " + e.getMessage(), e);
        }
    }

    @Override
    public void createActor(Actor actor) {
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    userServiceUrl + "/actor",
                    actor,
                    String.class
            );

            log.info("JSON enviado al microservicio USER: ", objectMapper.writeValueAsString(actor));

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Error al crear actor. Código: " + response.getStatusCode());
            }

            log.info("Actor creado exitosamente: ", actor.getEmail());

        } catch (Exception e) {
            log.error("Error al crear actor: ", actor.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Error al crear actor: " + e.getMessage(), e);
        }
    }

    @Override
    public Role findRoleByName(String roleName) {
        try {
            ResponseEntity<String> response = restTemplate
                    .getForEntity(roleServiceUrl + "/name/" + roleName, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return objectMapper.readValue(response.getBody(), Role.class);
            }
        } catch (HttpClientErrorException.NotFound e) {
            log.info("Rol no encontrado: {}", roleName);
            return null;
        } catch (Exception e) {
            log.error("Error al buscar rol {}: {}", roleName, e.getMessage(), e);
            throw new RuntimeException("Error al buscar rol: " + e.getMessage(), e);
        }

        return null;
    }

    @Override
    public Role createRole(Role role) {
        try {
            ResponseEntity<String> created = restTemplate
                    .postForEntity(roleServiceUrl, role, String.class);

            if (created.getStatusCode() == HttpStatus.CREATED && created.getBody() != null) {
                return objectMapper.readValue(created.getBody(), Role.class);
            } else {
                log.warn("No se pudo crear el rol: ", role.getName());
                throw new RuntimeException("No se pudo crear el rol.");
            }
        } catch (Exception e) {
            log.error("Error al crear rol: ", role.getName(), e.getMessage(), e);
            throw new RuntimeException("Error al crear rol: " + e.getMessage(), e);
        }
    }
}