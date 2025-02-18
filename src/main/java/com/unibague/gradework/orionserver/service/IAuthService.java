package com.unibague.gradework.orionserver.service;

import com.unibague.gradework.orionserver.model.LoginRequest;
import org.springframework.http.ResponseEntity;

/**
 * Interface defining authentication-related services.
 * Provides a method for user authentication.
 */
public interface IAuthService {

    /**
     * Authenticates a user based on the provided login credentials.
     *
     * @param loginRequest The login request containing the user's email and password.
     * @return A {@link ResponseEntity} containing the authentication response,
     * which may include a JWT token and user details if authentication is successful,
     * or an error message in case of failure.
     */
    ResponseEntity<?> authenticateUser(LoginRequest loginRequest);
}
