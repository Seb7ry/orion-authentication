package com.unibague.gradework.orionserver.service;

import com.unibague.gradework.orionserver.model.LoginRequest;
import com.unibague.gradework.orionserver.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

/**
 * Service for handling authentication logic.
 */
@Service
public class AuthService implements IAuthService {

    @Autowired
    private RestTemplate restTemplate;  // ✅ Calls the `user-manager` microservice

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String USER_SERVICE_URL = "http://user-manager/users";

    /**
     * Authenticates a user by validating credentials and generating a JWT token.
     */
    @Override
    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
        // Step 1: Fetch user from the `user-manager` microservice
        ResponseEntity<User> response = restTemplate.exchange(
                USER_SERVICE_URL + "/email?email=" + loginRequest.getEmail(),
                HttpMethod.GET,
                null,
                User.class
        );

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }

        User user = response.getBody();

        // Step 2: Validate password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }

        // Step 3: Generate JWT token
        String token = jwtService.generateToken(user);

        return ResponseEntity.ok().body("{\"token\": \"" + token + "\"}");
    }
}
