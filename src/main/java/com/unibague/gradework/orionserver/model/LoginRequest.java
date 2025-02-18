package com.unibague.gradework.orionserver.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Represents a login request containing the necessary credentials and role information.
 * This class is used for authentication purposes, encapsulating the user's email, password, and role.
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    /**
     * The email address of the user attempting to log in.
     */
    private String email;

    /**
     * The password associated with the user's account.
     */
    private String password;
}