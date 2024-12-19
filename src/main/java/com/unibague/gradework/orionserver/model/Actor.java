package com.unibague.gradework.orionserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Represents an Actor entity, which extends the {@link User} class.
 * This class stores additional details specific to an actor, such as
 * employee ID, program, and position within the organization.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Document(collection = "actors")
public class Actor extends User {

    /**
     * The unique identifier for the actor as an employee.
     */
    private Long employeeId;

    /**
     * The program associated with the actor, representing their area of responsibility or expertise.
     */
    private String program;

    /**
     * The position of the actor within the organization (e.g., "Administrator" or "Coordinator").
     */
    private String position;
}