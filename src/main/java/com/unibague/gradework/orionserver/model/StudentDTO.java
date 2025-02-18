package com.unibague.gradework.orionserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object (DTO) representing a student.
 * This class extends {@link UserDTO} to include additional attributes specific to students.
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class StudentDTO extends UserDTO {

    /**
     * The unique student identification number.
     */
    private String studentID;

    /**
     * The enrollment status of the student.
     * {@code true} indicates the student is active, while {@code false} means inactive.
     */
    private boolean status;

    /**
     * The current academic semester of the student.
     */
    private String semester;

    /**
     * The category of the student's academic program (e.g., Undergraduate, Graduate).
     */
    private String category;

    /**
     * Constructs a new {@code StudentDTO} instance with the specified attributes.
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
     * @param programs   The list of academic programs associated with the student.
     * @param studentID  The student's unique identification number.
     * @param status     The student's enrollment status.
     * @param semester   The student's current academic semester.
     * @param category   The student's academic category (e.g., Undergraduate).
     */
    public StudentDTO(String idUser, String firstName, String lastName, LocalDate birthDate,
                      String phone, String email, String image, String sex, String role,
                      List<ProgramDTO> programs, String studentID, boolean status,
                      String semester, String category) {
        super(idUser, firstName, lastName, birthDate, phone, email, image, sex, role, programs);
        this.studentID = studentID;
        this.status = status;
        this.semester = semester;
        this.category = category;
    }
}