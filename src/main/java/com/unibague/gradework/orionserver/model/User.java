package com.unibague.gradework.orionserver.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.unibague.gradework.orionserver.enumerator.TypeSex;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

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
     * This field is formatted as a string in the "yyyy-MM-dd" pattern when serialized to JSON.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    /**
     * The user's phone number.
     */
    private String phone;

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

    /**
     * List of program IDs associated with the user.
     *
     * This field stores references to programs that the user is enrolled in or affiliated with.
     * Instead of using @DBRef, only the program IDs are stored as Strings to avoid unnecessary
     * complexity and improve performance in MongoDB queries.
     *
     * Example:
     * - A student may be enrolled in multiple programs.
     * - An actor (employee) may be assigned to different programs.
     *
     * The IDs stored in this list should correspond to valid entries in the "programs" collection.
     */
    private List<String> programId;
}