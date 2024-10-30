package com.unibague.gradework.orionserver.model;

import com.unibague.gradework.orionserver.enumerator.TypeSex;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class User
{
    private Long idUser;
    private String firstName;
    private String lastName;
    private int phone;
    private String email;
    private String image;
    private TypeSex sex;
}
