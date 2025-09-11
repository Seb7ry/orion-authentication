package com.unibague.gradework.orionauth.user.models;

import com.unibague.gradework.orionauth.enumerator.TypeSex;
import com.unibague.gradework.orionauth.program.Program;
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
