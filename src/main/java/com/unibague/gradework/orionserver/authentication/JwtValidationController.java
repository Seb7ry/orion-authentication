package com.unibague.gradework.orionauth.authentication;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for JWT validation and key distribution
 * Used by the Gateway for token validation and JWKS endpoint
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class JwtValidationController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JwtKeyManager keyManager;

    /**
     * Validate JWT token - Used by Gateway
     * Headers: Authorization: Bearer <token>
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (!authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid authorization header format"));
            }

            String token = authHeader.substring(7);

            // Validate token and get all claims
            Map<String, Object> claims = jwtUtil.getAllClaims(token);

            // Prepare response with user context for Gateway
            Map<String, Object> validationResponse = new HashMap<>();
            validationResponse.put("valid", true);
            validationResponse.put("userId", claims.get("userId"));
            validationResponse.put("email", claims.get("sub")); // subject
            validationResponse.put("name", claims.get("name"));
            validationResponse.put("role", claims.get("role"));
            validationResponse.put("roleId", claims.get("roleId"));
            validationResponse.put("programs", claims.get("programs"));
            validationResponse.put("permissions", claims.get("permissions"));
            validationResponse.put("exp", claims.get("exp"));
            validationResponse.put("iat", claims.get("iat"));

            log.debug("Token validated successfully for user: {}", claims.get("userId"));
            return ResponseEntity.ok(validationResponse);

        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "valid", false,
                            "error", "Token validation failed",
                            "message", e.getMessage()
                    ));
        }
    }

    /**
     * JWKS endpoint - Provides public keys for token verification
     * Used by Gateway and other services
     */
    @GetMapping("/jwks")
    public ResponseEntity<Map<String, Object>> getJwks() {
        try {
            Map<String, Object> jwks = new HashMap<>();
            Map<String, Object> key = new HashMap<>();

            key.put("kty", "RSA");
            key.put("use", "sig");
            key.put("alg", "RS256");
            key.put("kid", "orion-auth-key-1");
            key.put("n", keyManager.getPublicKeyBase64());

            jwks.put("keys", java.util.List.of(key));

            log.debug("JWKS endpoint accessed");
            return ResponseEntity.ok(jwks);

        } catch (Exception e) {
            log.error("Error generating JWKS: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unable to generate JWKS"));
        }
    }

    /**
     * Refresh token endpoint
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        try {
            String refreshToken = request.get("refreshToken");
            if (refreshToken == null || refreshToken.isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Refresh token is required"));
            }

            // Validate refresh token
            if (!jwtUtil.isTokenValid(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid refresh token"));
            }

            String email = jwtUtil.extractEmail(refreshToken);

            // Here you would typically fetch fresh user data
            // For now, we'll just generate a new access token
            // You'll need to implement getUserByEmail to get fresh user data

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Token refresh endpoint - Implementation needed");
            response.put("email", email);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Token refresh failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token refresh failed"));
        }
    }

    /**
     * Logout endpoint - Invalidate token
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            if (!authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid authorization header"));
            }

            String token = authHeader.substring(7);
            String userId = jwtUtil.extractUserId(token);

            // Here you would typically add token to blacklist
            // For Phase 1, we'll just log the logout
            log.info("User {} logged out successfully", userId);

            return ResponseEntity.ok(Map.of(
                    "message", "Logged out successfully",
                    "userId", userId
            ));

        } catch (Exception e) {
            log.warn("Logout failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Logout failed"));
        }
    }

    /**
     * Health check for auth service
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "orion-auth");
        health.put("algorithm", "RS256");
        health.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(health);
    }
}