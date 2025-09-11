package com.unibague.gradework.orionauth.authentication;

import com.unibague.gradework.orionauth.user.models.UserLogDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JWT utility service with RS256 support
 * Now uses JwtKeyManager for RSA key management
 */
@Slf4j
@Component
public class JwtUtil {

    @Autowired
    private JwtKeyManager keyManager;

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
        // Initialize RSA keys on startup
        keyManager.initializeKeys();
        log.info("JwtUtil initialized with RS256 algorithm");
        log.info("Access token expiration: {} seconds", accessTokenExpiration);
        log.info("Refresh token expiration: {} seconds", refreshTokenExpiration);
    }

    /**
     * Generate access token with user information and claims
     */
    public String generateAccessToken(UserLogDTO user) {
        Map<String, Object> claims = new HashMap<>();

        // User basic info
        claims.put("userId", user.getIdUser());
        claims.put("email", user.getEmail());
        claims.put("name", user.getName());
        claims.put("type", "access");

        // Role information
        if (user.getRole() != null) {
            claims.put("role", user.getRole().getName());
            claims.put("roleId", user.getRole().getIdRole());
        }

        // Program information
        if (user.getPrograms() != null && !user.getPrograms().isEmpty()) {
            List<String> programIds = user.getPrograms().stream()
                    .map(program -> program.getProgramId())
                    .toList();
            claims.put("programs", programIds);
        }

        return createToken(claims, user.getEmail(), accessTokenExpiration * 1000);
    }

    /**
     * Generate simple token (compatible with existing code)
     */
    public String generateToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "simple");

        return createToken(claims, email, accessTokenExpiration * 1000);
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
     * Create JWT token with specified claims and expiration using RS256
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
                .signWith(SignatureAlgorithm.RS256, keyManager.getPrivateKey()) // Now using RS256!
                .compact();
    }

    /**
     * Validate token (compatible with existing code)
     */
    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(keyManager.getPublicKey())
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.debug("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extract email from token (compatible with existing code)
     */
    public String extractEmail(String token) {
        return Jwts.parser()
                .setSigningKey(keyManager.getPublicKey())
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Validate token and return claims - for gateway
     */
    public Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(keyManager.getPublicKey())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            throw new RuntimeException("Invalid token: " + e.getMessage());
        }
    }

    /**
     * Extract user ID from token
     */
    public String extractUserId(String token) {
        Claims claims = validateToken(token);
        return claims.get("userId", String.class);
    }

    /**
     * Extract user role from token
     */
    public String extractRole(String token) {
        Claims claims = validateToken(token);
        return claims.get("role", String.class);
    }

    /**
     * Extract user programs from token
     */
    @SuppressWarnings("unchecked")
    public List<String> extractPrograms(String token) {
        Claims claims = validateToken(token);
        return claims.get("programs", List.class);
    }

    /**
     * Check if token is expired
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
     * Check if token is a refresh token
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
     * Get all claims from token for validation endpoint
     */
    public Map<String, Object> getAllClaims(String token) {
        Claims claims = validateToken(token);
        return new HashMap<>(claims);
    }
}