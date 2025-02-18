package com.unibague.gradework.orionserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Represents a Student entity, which extends the {@link User} class.
 * This class stores additional details specific to a student, such as
 * their student ID, status, semester, category, and academic program.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Document(collection = "students")
public class Student extends User {

    /**
     * The unique identifier for the student.
     */
    private String studentID;

    /**
     * The status of the student, indicating if they are active or inactive.
     */
    private boolean status;

    /**
     * The semester in which the student is currently enrolled (e.g., "2nd Semester").
     */
    private String semester;

    /**
     * The category of the student (e.g., "Undergraduate", "Graduate").
     */
    private String category;

    /**
     * The academic program the student is enrolled in (e.g., "Computer Science").
     */
    private String program;
}