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

/**
 * Represents a User entity in the system.
 * This class serves as the base entity for various types of users,
 * providing common attributes like name, contact details, and role.
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Document(collection = "users")
public class User {

    /**
     * The unique identifier for the user.
     */
    @Id
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
     */
    private LocalDate birthDate;

    /**
     * The user's phone number.
     */
    private int phone;

    /**
     * The email address of the user.
     */
    private String email;

    /**
     * A URL or file path to the user's profile image.
     */
    private String image;

    /**
     * The password used by the user for authentication.
     */
    private String password;

    /**
     * The sex of the user, represented by the {@link TypeSex} enumerator.
     */
    private TypeSex sex;

    /**
     * The role assigned to the user, represented by a reference to the {@link Role} entity.
     */
    @DBRef
    private Role role;
}