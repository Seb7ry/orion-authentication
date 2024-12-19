package com.unibague.gradework.orionserver.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Represents a Role entity in the system.
 * A role defines a set of permissions or responsibilities for a user.
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "roles")
public class Role {

    /**
     * The unique identifier for the role.
     * This ID is used to differentiate roles in the system.
     */
    @Id
    private String idRole;

    /**
     * The name of the role (e.g., "Admin", "Actor", "Student").
     * This name is used to associate users with specific responsibilities or permissions.
     */
    private String name;
}