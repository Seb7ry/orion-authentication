package com.unibague.gradework.orionserver.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.unibague.gradework.orionserver.enumerator.TypeSex;
import com.unibague.gradework.orionserver.program.IProgramService;
import com.unibague.gradework.orionserver.program.Program;
import com.unibague.gradework.orionserver.user.IUserService;
import com.unibague.gradework.orionserver.user.models.Actor;
import com.unibague.gradework.orionserver.user.models.Role;
import com.unibague.gradework.orionserver.user.models.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApiMapperService implements IApiMapperService {

    @Autowired
    private IProgramService programService;

    @Autowired
    private IUserService userService;

    @Override
    public Student toStudent(JsonNode node, String imageUrl) {
        String programName = node.path("program").asText();
        String category = node.path("category").asText();

        Program program;
        try {
            program = programService.getProgramByName(programName);
        } catch (RuntimeException e) {
            program = programService.createProgram(new Program(null, programName));
        }

// Asegurarte que no sea null el ID
        if (program.getProgramId() == null) {
            throw new RuntimeException("No se pudo obtener el ID del programa para " + programName);
        }

        Role role = userService.findRoleByName(category);
        if (role == null) {
            role = userService.createRole(new Role(null, category));
        }

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
                .programs(program.getProgramId() != null ? List.of(program.getProgramId()) : List.of())
                .role(role)
                .build();
    }

    @Override
    public Actor toActor(JsonNode node, String imageUrl) {
        String programName = node.path("program").asText();
        String position = node.path("position").asText();

        Program program;
        try {
            program = programService.getProgramByName(programName);
        } catch (RuntimeException e) {
            program = programService.createProgram(new Program(null, programName));
        }

        // Asegurarte que no sea null el ID
        if (program.getProgramId() == null) {
            throw new RuntimeException("No se pudo obtener el ID del programa para " + programName);
        }

        Role role = userService.findRoleByName(position);
        if (role == null) {
            role = userService.createRole(new Role(null, position));
        }

        return Actor.builder()
                .idUser(node.path("identification").asText())
                .name(node.path("full_name").asText())
                .email(node.path("email").asText())
                .phone(node.path("extension").asText())
                .sex("F".equalsIgnoreCase(node.path("sex").asText()) ? TypeSex.FEMALE : TypeSex.MALE)
                .image(imageUrl != null ? imageUrl : "")
                .position(position)
                .password("")
                .programs(program.getProgramId() != null ? List.of(program.getProgramId()) : List.of())
                .role(role)
                .build();
    }
}