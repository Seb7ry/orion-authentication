package com.unibague.gradework.orionserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object (DTO) representing an administrative actor.
 * This class extends {@link UserDTO} to include additional attributes specific to actors.
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ActorDTO extends UserDTO {

    /**
     * The unique identifier for the employee.
     */
    private String employeeId;

    /**
     * The position or job title of the actor.
     */
    private String position;

    /**
     * Constructs a new {@code ActorDTO} instance with the specified attributes.
     *
     * @param idUser     The user's unique identifier.
     * @param firstName  The user's first name.
     * @param lastName   The user's last name.
     * @param birthDate  The user's birth date.
     * @param phone      The user's phone number.
     * @param email      The user's email address.
     * @param image      The URL of the user's profile image.
     * @param sex        The user's gender.
     * @param role       The user's role.
     * @param programs   The list of academic programs associated with the user.
     * @param employeeId The unique employee identifier.
     * @param position   The position or job title of the actor.
     */
    public ActorDTO(String idUser, String firstName, String lastName, LocalDate birthDate,
                    String phone, String email, String image, String sex, String role,
                    List<ProgramDTO> programs, String employeeId, String position) {
        super(idUser, firstName, lastName, birthDate, phone, email, image, sex, role, programs);
        this.employeeId = employeeId;
        this.position = position;
    }
}
