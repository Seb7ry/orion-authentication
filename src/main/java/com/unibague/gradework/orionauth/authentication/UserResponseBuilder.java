package com.unibague.gradework.orionauth.authentication;

import com.unibague.gradework.orionauth.program.Program;
import com.unibague.gradework.orionauth.user.models.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UserResponseBuilder {

    public Map<String, Object> buildUserResponse(UserLogDTO user) {
        Map<String, Object> simpleAttributes = new HashMap<>();
        simpleAttributes.put("idUser", user.getIdUser());
        simpleAttributes.put("email", user.getEmail());
        simpleAttributes.put("name", user.getName());
        simpleAttributes.put("phone", user.getPhone());
        simpleAttributes.put("image", user.getImage());
        simpleAttributes.put("sex", user.getSex());
        simpleAttributes.put("role", user.getRole() != null ? user.getRole().getName() : null);

        List<Map<String, Object>> programList = new ArrayList<>();
        if (user.getPrograms() != null) {
            for (Program program : user.getPrograms()) {
                Map<String, Object> programData = new HashMap<>();
                programData.put("programId", program.getProgramId());
                programData.put("programName", program.getProgramName());
                programList.add(programData);
            }
        }
        simpleAttributes.put("programs", programList);

        if (user instanceof StudentLogDTO student) {
            simpleAttributes.put("studentID", student.getStudentID());
            simpleAttributes.put("semester", student.getSemester());
            simpleAttributes.put("status", student.isStatus());
        } else if (user instanceof ActorLogDTO actor) {
            simpleAttributes.put("position", actor.getPosition());
        }

        return simpleAttributes;
    }
}