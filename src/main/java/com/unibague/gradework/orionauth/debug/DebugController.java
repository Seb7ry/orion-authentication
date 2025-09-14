package com.unibague.gradework.orionauth.debug;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * TEMPORAL - Controller para debug de conectividad
 * ELIMINAR EN PRODUCCIÓN
 */
@Slf4j
@RestController
@RequestMapping("/auth/debug")
public class DebugController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${user.service.url:http://localhost:8080/api/users}")
    private String userServiceUrl;

    @Value("${gateway.service.token:auth-service-token-secure-2025}")
    private String serviceToken;

    /**
     * Test básico de conectividad al Gateway
     */
    @GetMapping("/test-gateway")
    public ResponseEntity<?> testGateway() {
        Map<String, Object> result = new HashMap<>();

        try {
            // Test 1: Gateway health
            String healthUrl = "http://localhost:8080/actuator/health";
            log.info("🧪 Testing Gateway Health: {}", healthUrl);

            ResponseEntity<String> healthResponse = restTemplate.getForEntity(healthUrl, String.class);

            result.put("gatewayHealth", Map.of(
                    "url", healthUrl,
                    "status", healthResponse.getStatusCode().value(),
                    "success", true
            ));

        } catch (Exception e) {
            log.error("❌ Gateway Health Test Failed: {}", e.getMessage());
            result.put("gatewayHealth", Map.of(
                    "url", "http://localhost:8080/actuator/health",
                    "error", e.getMessage(),
                    "success", false
            ));
        }

        try {
            // Test 2: User Service através del Gateway (debería fallar con 401)
            String userUrl = userServiceUrl + "/health";
            log.info("🧪 Testing User Service via Gateway: {}", userUrl);

            ResponseEntity<String> userResponse = restTemplate.getForEntity(userUrl, String.class);

            result.put("userServiceViaGateway", Map.of(
                    "url", userUrl,
                    "status", userResponse.getStatusCode().value(),
                    "body", userResponse.getBody(),
                    "success", true
            ));

        } catch (Exception e) {
            log.warn("⚠️ User Service Test (expected to fail): {}", e.getMessage());
            result.put("userServiceViaGateway", Map.of(
                    "url", userServiceUrl + "/health",
                    "error", e.getMessage(),
                    "expectedToFail", true,
                    "success", false
            ));
        }

        // Test 3: Configuration check
        result.put("configuration", Map.of(
                "userServiceUrl", userServiceUrl,
                "serviceToken", serviceToken != null ? serviceToken.substring(0, 10) + "..." : "null",
                "restTemplateConfigured", restTemplate != null
        ));

        return ResponseEntity.ok(result);
    }

    /**
     * Test específico del endpoint que falla en login
     */
    @GetMapping("/test-user-email")
    public ResponseEntity<?> testUserEmailEndpoint() {
        String email = "admin@unibague.edu.co";
        String testUrl = userServiceUrl + "/auth/email/" + email;

        Map<String, Object> result = new HashMap<>();

        try {
            log.info("🧪 Testing User Email Endpoint: {}", testUrl);
            log.info("🔑 Using service token: {}", serviceToken != null ? serviceToken.substring(0, 10) + "..." : "null");

            ResponseEntity<String> response = restTemplate.getForEntity(testUrl, String.class);

            result.put("success", true);
            result.put("url", testUrl);
            result.put("status", response.getStatusCode().value());
            result.put("body", response.getBody());
            result.put("message", "Unexpected success - this usually fails with 401");

        } catch (Exception e) {
            log.error("❌ User Email Test Failed (as expected): {}", e.getMessage());

            result.put("success", false);
            result.put("url", testUrl);
            result.put("error", e.getMessage());
            result.put("errorType", e.getClass().getSimpleName());
            result.put("message", "This is the same error that occurs during login");
        }

        return ResponseEntity.ok(result);
    }

    /**
     * Información del RestTemplate configurado
     */
    @GetMapping("/resttemplate-info")
    public ResponseEntity<?> restTemplateInfo() {
        Map<String, Object> info = new HashMap<>();

        info.put("restTemplateConfigured", restTemplate != null);
        info.put("interceptorCount", restTemplate != null ? restTemplate.getInterceptors().size() : 0);
        info.put("userServiceUrl", userServiceUrl);
        info.put("serviceToken", serviceToken != null ? "SET (" + serviceToken.length() + " chars)" : "NOT SET");

        if (restTemplate != null) {
            info.put("interceptors", restTemplate.getInterceptors().stream()
                    .map(i -> i.getClass().getSimpleName())
                    .toList());
        }

        return ResponseEntity.ok(info);
    }
}