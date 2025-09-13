package com.unibague.gradework.orionauth.authentication;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * TEMPORARY UTILITY CONTROLLER
 * Generates password hashes for initial users
 * REMOVE IN PRODUCTION OR SECURE IT PROPERLY
 */
@Slf4j
@RestController
@RequestMapping("/auth/utils")
public class PasswordHashController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Generate BCrypt hash for a password
     * Use this to create hashes for initial users
     */
    @PostMapping("/hash-password")
    public ResponseEntity<?> generateHash(@RequestBody Map<String, String> request) {
        String plainPassword = request.get("password");

        if (plainPassword == null || plainPassword.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Password is required"));
        }

        String hashedPassword = passwordEncoder.encode(plainPassword);

        log.warn("PASSWORD HASH GENERATED - DEVELOPMENT ONLY");
        log.warn("Plain: {}", plainPassword);
        log.warn("Hash: {}", hashedPassword);

        return ResponseEntity.ok(Map.of(
                "plainPassword", plainPassword,
                "hashedPassword", hashedPassword,
                "usage", "Use this hash in your MongoDB initialization script",
                "warning", "REMOVE THIS ENDPOINT IN PRODUCTION"
        ));
    }

    /**
     * Generate multiple common password hashes
     */
    @PostMapping("/common-hashes")
    public ResponseEntity<?> generateCommonHashes() {
        String[] commonPasswords = {
                "OrionAdmin2025!",
                "Coordinator123!",
                "Teacher2025!",
                "Student123!"
        };

        Map<String, String> hashes = new java.util.HashMap<>();

        for (String password : commonPasswords) {
            hashes.put(password, passwordEncoder.encode(password));
        }

        log.warn("MULTIPLE PASSWORD HASHES GENERATED - DEVELOPMENT ONLY");

        return ResponseEntity.ok(Map.of(
                "hashes", hashes,
                "usage", "Use these hashes for initial users in MongoDB",
                "warning", "REMOVE THIS ENDPOINT IN PRODUCTION"
        ));
    }
}