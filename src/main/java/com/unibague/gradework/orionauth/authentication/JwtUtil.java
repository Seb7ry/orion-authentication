package com.unibague.gradework.orionauth.authentication;

import com.unibague.gradework.orionauth.user.models.UserLogDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Enhanced JWT utility service with RS256 support
 * Compatible with existing model structure
 */
@Slf4j
@Component
public class JwtUtil {

    // Temporal: Usando clave fija hasta implementar JwtKeyManager
    private final String SECRET = "gradeworksupersecurekeygradeworksupersecurekey";
    private final long EXPIRATION = 1000 * 60 * 60 * 10; // 10 horas

    @Value("${jwt.issuer:orion-auth}")
    private String issuer;

    @Value("${jwt.audience:orion-services}")
    private String audience;

    @Value("${jwt.access-token-expiration:3600}")
    private long accessTokenExpiration; // seconds

    @Value("${jwt.refresh-token-expiration:86400}")
    private long refreshTokenExpiration; // seconds

    @PostConstruct
    public void init() {
        log.info("JwtUtil initialized - Migration Phase 1");
        log.info("Access token expiration: {} seconds", accessTokenExpiration);
        log.info("Refresh token expiration: {} seconds", refreshTokenExpiration);
        log.warn("USING TEMPORARY HS256 - WILL MIGRATE TO RS256 IN NEXT STEP");
    }

    /**
     * Generate access token with user information and claims
     * FASE 1: Usar estructura existente, mejorar en FASE 2
     */
    public String generateAccessToken(UserLogDTO user) {
        Map<String, Object> claims = new HashMap<>();

        // User basic info
        claims.put("userId", user.getIdUser());
        claims.put("email", user.getEmail());
        claims.put("name", user.getName());
        claims.put("type", "access");

        // Role information - Usar estructura actual
        if (user.getRole() != null) {
            claims.put("role", user.getRole().getName());
            claims.put("roleId", user.getRole().getIdRole());
        }

        // Program information - Adaptar a estructura actual
        if (user.getPrograms() != null && !user.getPrograms().isEmpty()) {
            List<String> programIds = user.getPrograms().stream()
                    .map(program -> program.getProgramId())
                    .toList();
            claims.put("programs", programIds);
        }

        return createToken(claims, user.getEmail(), accessTokenExpiration * 1000);
    }

    /**
     * Generate simple token (compatible con código actual)
     */
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, SECRET.getBytes())
                .compact();
    }

    /**
     * Generate refresh token
     */
    public String generateRefreshToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");

        return createToken(claims, email, refreshTokenExpiration * 1000);
    }

    /**
     * Create JWT token with specified claims and expiration
     * FASE 1: HS256 temporal, FASE 2: RS256
     */
    private String createToken(Map<String, Object> claims, String subject, long expirationMs) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer(issuer)
                .setAudience(audience)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, SECRET.getBytes()) // Temporal HS256
                .compact();
    }

    /**
     * Validate token (compatible con código actual)
     */
    public boolean isTokenValid(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET.getBytes()).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extract email from token (compatible con código actual)
     */
    public String extractEmail(String token) {
        return Jwts.parser().setSigningKey(SECRET.getBytes())
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Validate token and return claims - NUEVO para gateway
     */
    public Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET.getBytes())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            throw new RuntimeException("Invalid token: " + e.getMessage());
        }
    }

    /**
     * Extract user ID from token - NUEVO
     */
    public String extractUserId(String token) {
        Claims claims = validateToken(token);
        return claims.get("userId", String.class);
    }

    /**
     * Extract user role from token - NUEVO
     */
    public String extractRole(String token) {
        Claims claims = validateToken(token);
        return claims.get("role", String.class);
    }

    /**
     * Extract user programs from token - NUEVO
     */
    @SuppressWarnings("unchecked")
    public List<String> extractPrograms(String token) {
        Claims claims = validateToken(token);
        return claims.get("programs", List.class);
    }

    /**
     * Check if token is expired - NUEVO
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = validateToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Check if token is a refresh token - NUEVO
     */
    public boolean isRefreshToken(String token) {
        try {
            Claims claims = validateToken(token);
            return "refresh".equals(claims.get("type", String.class));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get all claims from token for validation endpoint - NUEVO
     */
    public Map<String, Object> getAllClaims(String token) {
        Claims claims = validateToken(token);
        return new HashMap<>(claims);
    }
}