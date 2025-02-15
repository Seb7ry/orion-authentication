package com.unibague.gradework.orionserver.controller;

import com.unibague.gradework.orionserver.model.LoginRequest;
import com.unibague.gradework.orionserver.service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller responsible for handling authentication-related endpoints.
 * Provides login and logout functionalities.
 */
@RestController
@RequestMapping("/authenticate")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private IAuthService authService;

    /**
     * Endpoint to authenticate a user and return a JWT token.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        return authService.authenticateUser(loginRequest);
    }
}