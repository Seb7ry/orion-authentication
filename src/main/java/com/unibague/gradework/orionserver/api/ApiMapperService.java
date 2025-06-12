package com.unibague.gradework.orionserver.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.unibague.gradework.orionserver.enumerator.TypeSex;
import com.unibague.gradework.orionserver.program.IProgramService;
import com.unibague.gradework.orionserver.program.Program;
import com.unibague.gradework.orionserver.user.IUserService;
import com.unibague.gradework.orionserver.user.models.Actor;
import com.unibague.gradework.orionserver.user.models.Role;
import com.unibague.gradework.orionserver.user.models.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApiMapperService implements IApiMapperService {

    private static final Logger log = LoggerFactory.getLogger(ApiMapperService.class);

    private final IProgramService programService;
    private final IUserService userService;

    public ApiMapperService(IProgramService programService, IUserService userService) {
        this.programService = programService;
        this.userService = userService;
    }

    @Override
    public Student toStudent(JsonNode node, String imageUrl) {
        try {
            String programName = node.path("program").asText();
            String category = node.path("category").asText();

            Program program = getOrCreateProgram(programName);
            Role role = getOrCreateRole(category);

            return Student.builder()
                    .idUser(node.path("identification").asText())
                    .name(node.path("name").asText())
                    .email(node.path("email").asText())
                    .phone(node.path("telephone").asText())
                    .sex("F".equalsIgnoreCase(node.path("sexo").asText()) ? TypeSex.FEMALE : TypeSex.MALE)
                    .image(imageUrl != null ? imageUrl : "")
                    .studentID(node.path("code_student").asText())
                    .semester(node.path("semester").asText())
                    .status("Activo".equalsIgnoreCase(node.path("status").asText()))
                    .password("")
                    .programs(List.of(program.getProgramId()))
                    .role(role)
                    .build();
        } catch (Exception e) {
            log.error("Error al mapear estudiante: ", e.getMessage(), e);
            throw new RuntimeException("Error al mapear estudiante desde API externa.", e);
        }
    }

    @Override
    public Actor toActor(JsonNode node, String imageUrl) {
        try {
            String programName = node.path("program").asText();
            String position = node.path("position").asText();

            Program program = getOrCreateProgram(programName);
            Role role = getOrCreateRole(position);

            return Actor.builder()
                    .idUser(node.path("identification").asText())
                    .name(node.path("full_name").asText())
                    .email(node.path("email").asText())
                    .phone(node.path("extension").asText())
                    .sex("F".equalsIgnoreCase(node.path("sex").asText()) ? TypeSex.FEMALE : TypeSex.MALE)
                    .image(imageUrl != null ? imageUrl : "")
                    .position(position)
                    .password("")
                    .programs(List.of(program.getProgramId()))
                    .role(role)
                    .build();
        } catch (Exception e) {
            log.error("Error al mapear actor: ", e.getMessage(), e);
            throw new RuntimeException("Error al mapear actor desde API externa.", e);
        }
    }

    private Program getOrCreateProgram(String programName) {
        try {
            return programService.getProgramByName(programName);
        } catch (RuntimeException e) {
            log.warn("Programa no encontrado, se intentará crear: ", programName);
            return programService.createProgram(new Program(null, programName));
        }
    }

    private Role getOrCreateRole(String roleName) {
        Role role = userService.findRoleByName(roleName);
        if (role == null) {
            log.warn("Rol no encontrado, se intentará crear: ", roleName);
            role = userService.createRole(new Role(null, roleName));
        }
        return role;
    }
}