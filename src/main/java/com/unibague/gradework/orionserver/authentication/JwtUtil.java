package com.unibague.gradework.orionserver.authentication;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private final String SECRET = "gradeworksupersecurekeygradeworksupersecurekey"; // 256 bits mínimo
    private final long EXPIRATION = 1000 * 60 * 60 * 10; // 10 horas

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, SECRET.getBytes())
                .compact();
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET.getBytes()).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractEmail(String token) {
        return Jwts.parser().setSigningKey(SECRET.getBytes())
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
