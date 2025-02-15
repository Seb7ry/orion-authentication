package com.unibague.gradework.orionserver.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@RequiredArgsConstructor
public class UserDTO {
    private String idUser;
    private String firstName;
    private String lastName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    private String phone;
    private String email;
    private String image;
    private String sex;
    private String role;
    private List<ProgramDTO> programs;

    public UserDTO(String idUser, String firstName, String lastName, LocalDate birthDate,
                   String phone, String email, String image, String sex, String role,
                   List<ProgramDTO> programs) {
        this.idUser = idUser;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.phone = phone;
        this.email = email;
        this.image = image;
        this.sex = sex;
        this.role = role;
        this.programs = programs;
    }
}
