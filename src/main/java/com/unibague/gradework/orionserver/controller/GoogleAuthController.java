package com.unibague.gradework.orionserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for handling Google OAuth authentication.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class GoogleAuthController {

    /**
     * Retrieves user information after Google authentication.
     *
     * @param authenticationToken The OAuth2AuthenticationToken containing user details.
     * @return A response entity containing user information.
     */
    @GetMapping("/google/user")
    public ResponseEntity<?> getUserInfo(OAuth2AuthenticationToken authenticationToken) {
        OidcUser oidcUser = (OidcUser) authenticationToken.getPrincipal();
        return ResponseEntity.ok(Map.of(
                "name", oidcUser.getFullName(),
                "email", oidcUser.getEmail(),
                "picture", oidcUser.getPicture()
        ));
    }
}
