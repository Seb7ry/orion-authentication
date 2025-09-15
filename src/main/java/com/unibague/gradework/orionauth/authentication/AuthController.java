package com.unibague.gradework.orionauth.authentication;

import com.unibague.gradework.orionauth.user.models.UserLogDTO;
import com.unibague.gradework.orionauth.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private IAuthService authService;

    @Autowired
    private IUserService userService;

    @Autowired
    private UserResponseBuilder userResponseBuilder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        if(loginRequest.getEmail() == null || loginRequest.getEmail().isBlank() ||
                loginRequest.getPassword() == null || loginRequest.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body("Email y contraseña son obligatorios.");
        }

        try {
            Map<String, Object> userData = authService.authenticate(loginRequest);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(userData);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(OAuth2AuthenticationToken authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        try {
            // Obtener email del usuario autenticado por OAuth2
            String email = (String) authentication.getPrincipal().getAttributes().get("email");

            if (email == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Email not found in OAuth2 attributes"));
            }

            // Obtener información completa del usuario desde la base de datos
            UserLogDTO user = userService.getUserByEmail(email);

            // Construir respuesta con información del usuario
            Map<String, Object> userResponse = userResponseBuilder.buildUserResponse(user);

            // Generar tokens JWT
            String accessToken = jwtUtil.generateAccessToken(user);
            String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

            // Agregar tokens a la respuesta
            userResponse.put("accessToken", accessToken);
            userResponse.put("refreshToken", refreshToken);
            userResponse.put("tokenType", "Bearer");
            userResponse.put("expiresIn", 3600);
            userResponse.put("authMethod", "google_oauth2");

            return ResponseEntity.ok(userResponse);

        } catch (Exception e) {
            // Si falla obtener el usuario de la BD, devolver atributos básicos de Google
            Map<String, Object> attributes = authentication.getPrincipal().getAttributes();
            return ResponseEntity.ok(Map.of(
                    "basicAttributes", attributes,
                    "error", "Could not retrieve full user data: " + e.getMessage()
            ));
        }
    }
}