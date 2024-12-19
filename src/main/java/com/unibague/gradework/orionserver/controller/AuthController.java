package com.unibague.gradework.orionserver.controller;

import com.unibague.gradework.orionserver.model.LoginRequest;
import com.unibague.gradework.orionserver.model.User;
import com.unibague.gradework.orionserver.repository.ActorRepository;
import com.unibague.gradework.orionserver.repository.StudentRepository;
import com.unibague.gradework.orionserver.repository.UserRepository;
import com.unibague.gradework.orionserver.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * Controller responsible for handling authentication-related endpoints.
 * Provides login and logout functionalities.
 */
@RestController
@RequestMapping("/authenticate")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Authenticates a user and generates a JWT token if credentials are valid.
     *
     * @param loginRequest the {@link LoginRequest} containing the user's email, password, and role.
     * @return a JWT token as a {@link String}.
     * @throws RuntimeException if the user is not found or credentials are invalid.
     */
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest) {
        Optional<User> user = userRepository.findByEmail(loginRequest.getEmail());

        if (user.isEmpty()) {
            user = studentRepository.findByEmail(loginRequest.getEmail())
                    .map(student -> (User) student);
        }

        if (user.isEmpty()) {
            user = actorRepository.findByEmail(loginRequest.getEmail())
                    .map(actor -> (User) actor);
        }

        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User userEntity = user.orElseThrow(() -> new RuntimeException("User not found"));

        if (!loginRequest.getPassword().equals(userEntity.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(userEntity.getEmail(), loginRequest.getRole());
        return token;
    }

    /**
     * Logs out the user by clearing the security context.
     *
     * @return a {@link String} indicating a successful logout.
     */
    @PostMapping("/logout")
    public String logout() {
        SecurityContextHolder.clearContext();
        return "Logout successful";
    }
}