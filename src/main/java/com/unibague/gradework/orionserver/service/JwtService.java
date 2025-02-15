package com.unibague.gradework.orionserver.service;

import com.unibague.gradework.orionserver.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

/**
 * Service for handling JSON Web Token (JWT) operations such as token generation, validation,
 * and claims extraction. This class is essential for implementing authentication and authorization.
 */
@Service
@RequiredArgsConstructor
public class JwtService {

    /**
     * The secret key used for signing JWTs.
     * Retrieved from the application's configuration properties.
     */
    @Value("${jwt.secret}")
    private String secretKey;

    /**
     * The token expiration time in milliseconds.
     * Retrieved from the application's configuration properties.
     */
    @Value("${jwt.token.expiration}")
    private long expirationTime;

    /**
     * Retrieves the signing key used to sign and verify JWTs.
     *
     * @return a {@link Key} object derived from the secret key.
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail()) // Usamos el email como "subject"
                .claim("idUser", user.getIdUser()) // ID del usuario
                .claim("role", user.getRole().getName()) // Nombre del rol
                .claim("firstName", user.getFirstName()) // Nombre del usuario
                .claim("lastName", user.getLastName()) // Apellido
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extracts all claims from a given JWT.
     *
     * @param token the JWT to extract claims from.
     * @return a {@link Claims} object containing the token's claims.
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Extracts the role claim from a given JWT.
     *
     * @param token the JWT to extract the role from.
     * @return the role as a {@link String}.
     */
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    /**
     * Validates a JWT by checking its email claim and expiration status.
     *
     * @param token the JWT to validate.
     * @param email the email to match against the token's subject.
     * @return {@code true} if the token is valid, {@code false} otherwise.
     */
    public boolean isTokenValid(String token, String email) {
        final String username = extractAllClaims(token).getSubject();
        return (username.equals(email) && !isTokenExpired(token));
    }

    /**
     * Checks if a JWT has expired.
     *
     * @param token the JWT to check.
     * @return {@code true} if the token has expired, {@code false} otherwise.
     */
    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }
}