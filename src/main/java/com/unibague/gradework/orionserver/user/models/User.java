package com.unibague.gradework.orionserver.user.models;

import com.unibague.gradework.orionserver.enumerator.TypeSex;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class User {
    private String idUser;
    private String name;
    private String email;
    private String phone;
    private String image;
    private TypeSex sex;
    private String password;
    private Role role;
    private List<String> programs;
}