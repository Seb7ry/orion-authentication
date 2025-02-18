package com.unibague.gradework.orionserver.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Represents the response returned after a successful authentication.
 * This class contains the JWT token and user details.
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    /**
     * The JWT token generated upon successful authentication.
     */
    private String token;

    /**
     * The authenticated user's details.
     * This can be an instance of either {@link StudentDTO} or {@link ActorDTO}.
     */
    private Object user;
}
