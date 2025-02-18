package com.unibague.gradework.orionserver.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object (DTO) representing a user.
 * This class encapsulates user-related data for communication between services.
 */
@Data
@RequiredArgsConstructor
public class UserDTO {

    /**
     * The unique identifier of the user.
     */
    private String idUser;

    /**
     * The first name of the user.
     */
    private String firstName;

    /**
     * The last name of the user.
     */
    private String lastName;

    /**
     * The birth date of the user.
     * This field is serialized as a string in the "yyyy-MM-dd" format.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    /**
     * The user's phone number.
     */
    private String phone;

    /**
     * The user's email address.
     */
    private String email;

    /**
     * The URL of the user's profile image.
     */
    private String image;

    /**
     * The user's gender (e.g., MALE, FEMALE, OTHER).
     */
    private String sex;

    /**
     * The user's role (e.g., Student, Actor).
     */
    private String role;

    /**
     * The list of academic programs associated with the user.
     */
    private List<ProgramDTO> programs;

    /**
     * Constructs a new UserDTO with the specified attributes.
     *
     * @param idUser    The user's unique identifier.
     * @param firstName The user's first name.
     * @param lastName  The user's last name.
     * @param birthDate The user's birth date.
     * @param phone     The user's phone number.
     * @param email     The user's email address.
     * @param image     The URL of the user's profile image.
     * @param sex       The user's gender.
     * @param role      The user's role.
     * @param programs  The list of programs associated with the user.
     */
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
