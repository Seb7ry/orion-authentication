package com.unibague.gradework.orionserver.user.models;

import com.unibague.gradework.orionserver.enumerator.TypeSex;
import com.unibague.gradework.orionserver.program.Program;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserLogDTO {
    private String idUser;
    private String name;
    private String email;
    private String phone;
    private String image;
    private TypeSex sex;
    private Role role;
    private List<Program> programs;
    private String password;
}
