package com.unibague.gradework.orionserver.model;

import com.unibague.gradework.orionserver.enumerator.TypeSex;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Document(collection = "users")
public class User
{
    @Id
    private String idUser;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private int phone;
    private String email;
    private String image;
    private String password;
    private TypeSex sex;

    @DBRef
    private Role role;
}
