package com.unibague.gradework.orionserver.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unibague.gradework.orionserver.user.models.*;
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
    private static final String ROLE_SERVICE_URL = "http://localhost:8092/service/role";

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

    @Override
    public void createStudent(Student student) {
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    USER_SERVICE_URL + "/student",
                    student,
                    String.class
            );
            System.out.println(">>> JSON enviado al microservicio USER:");
            System.out.println(objectMapper.writeValueAsString(response));

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Error al crear estudiante. Código: " + response.getStatusCode());
            }

            System.out.println(">>> Estudiante creado exitosamente: " + student.getEmail());

        } catch (Exception e) {
            throw new RuntimeException("Error al crear estudiante: " + e.getMessage(), e);
        }
    }

    @Override
    public void createActor(Actor actor) {
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    USER_SERVICE_URL + "/actor",
                    actor,
                    String.class
            );
            System.out.println(">>> JSON enviado al microservicio USER:");
            System.out.println(objectMapper.writeValueAsString(response)); // o actor si es el caso

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Error al crear actor. Código: " + response.getStatusCode());
            }

            System.out.println(">>> Actor creado exitosamente: " + actor.getEmail());

        } catch (Exception e) {
            throw new RuntimeException("Error al crear actor: " + e.getMessage(), e);
        }
    }

    @Override
    public Role findRoleByName(String roleName) {
        try {
            ResponseEntity<String> response = restTemplate
                    .getForEntity(ROLE_SERVICE_URL + "/name/" + roleName, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return objectMapper.readValue(response.getBody(), Role.class);
            }
        } catch (HttpClientErrorException.NotFound e) {
            System.out.println(">>> Rol no encontrado: " + roleName);
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar rol: " + e.getMessage(), e);
        }

        return null;
    }

    @Override
    public Role createRole(Role role) {
        try {
            ResponseEntity<String> created = restTemplate
                    .postForEntity(ROLE_SERVICE_URL, role, String.class);

            if (created.getStatusCode() == HttpStatus.CREATED && created.getBody() != null) {
                return objectMapper.readValue(created.getBody(), Role.class);
            } else {
                throw new RuntimeException("No se pudo crear el rol.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al crear rol: " + e.getMessage(), e);
        }
    }
}
