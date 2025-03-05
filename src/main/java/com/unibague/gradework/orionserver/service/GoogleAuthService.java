package com.unibague.gradework.orionserver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Service responsible for handling Google OAuth authentication.
 */
@Service
@RequiredArgsConstructor
public class GoogleAuthService {

    private final RestTemplate restTemplate;

    // Load environment variables securely
    private static final String CLIENT_ID = System.getenv("GOOGLE_CLIENT_ID");
    private static final String CLIENT_SECRET = System.getenv("GOOGLE_CLIENT_SECRET");
    private static final String REDIRECT_URI = System.getenv("GOOGLE_REDIRECT_URI");

    private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String USER_INFO_URL = "https://www.googleapis.com/oauth2/v3/userinfo";

    /**
     * Exchanges the authorization code for an access token and validates the user's email domain.
     *
     * @param code The authorization code received from Google.
     * @return The access token if the email domain is valid.
     * @throws RuntimeException if any error occurs during the authentication process.
     */
    public String exchangeCodeForToken(String code) {
        if (CLIENT_ID == null || CLIENT_SECRET == null || REDIRECT_URI == null) {
            throw new RuntimeException("Google OAuth credentials are missing. Ensure environment variables are set.");
        }

        // Request parameters for token exchange
        Map<String, String> params = new HashMap<>();
        params.put("code", code);
        params.put("client_id", CLIENT_ID);
        params.put("client_secret", CLIENT_SECRET);
        params.put("redirect_uri", REDIRECT_URI);
        params.put("grant_type", "authorization_code");

        // Configure headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(params, headers);

        // Request the access token
        ResponseEntity<Map> response = restTemplate.exchange(TOKEN_URL, HttpMethod.POST, request, Map.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Failed to retrieve Google access token.");
        }

        // Ensure access token exists
        String accessToken = Objects.requireNonNull(response.getBody().get("access_token")).toString();

        return validateUserAndGenerateToken(accessToken);
    }

    /**
     * Validates the user's email domain and generates an access token.
     *
     * @param accessToken The access token obtained from Google.
     * @return The validated access token.
     */
    private String validateUserAndGenerateToken(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        // Retrieve user information
        ResponseEntity<Map> userInfoResponse = restTemplate.exchange(
                USER_INFO_URL, HttpMethod.GET, new HttpEntity<>(headers), Map.class);

        if (!userInfoResponse.getStatusCode().is2xxSuccessful() || userInfoResponse.getBody() == null) {
            throw new RuntimeException("Failed to retrieve user information.");
        }

        // Ensure email exists
        String email = Objects.requireNonNull(userInfoResponse.getBody().get("email")).toString();

        // Validate allowed email domains
        if (!(email.endsWith("@unibague.edu.co") || email.endsWith("@estudiantesunibague.edu.co"))) {
            throw new RuntimeException("Access denied. Only @unibague.edu.co or @estudiantesunibague.edu.co emails are allowed.");
        }

        return accessToken;
    }
}
