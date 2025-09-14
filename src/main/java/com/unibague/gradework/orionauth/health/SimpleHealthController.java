package com.unibague.gradework.orionauth.health;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple health and connectivity controller
 * No security required - public endpoints
 */
@Slf4j
@RestController
public class SimpleHealthController {

    private final RestTemplate restTemplate;

    @Value("${user.service.url:http://localhost:8080/api/users}")
    private String userServiceUrl;

    public SimpleHealthController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Simple health check
     */
    @GetMapping("/health-simple")
    public ResponseEntity<Map<String, Object>> healthSimple() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "orion-auth");
        health.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(health);
    }

    /**
     * Test Gateway connectivity - NO SECURITY
     */
    @GetMapping("/test-gateway-simple")
    public ResponseEntity<Map<String, Object>> testGatewaySimple() {
        Map<String, Object> result = new HashMap<>();

        try {
            // Test básico de conectividad al Gateway
            String gatewayHealthUrl = "http://localhost:8080/actuator/health";
            log.info("🧪 Testing Gateway at: {}", gatewayHealthUrl);

            ResponseEntity<String> response = restTemplate.getForEntity(gatewayHealthUrl, String.class);

            result.put("gatewayConnectivity", "SUCCESS");
            result.put("gatewayStatus", response.getStatusCode().value());
            result.put("gatewayResponse", response.getBody());
            result.put("message", "Gateway is reachable");

        } catch (Exception e) {
            log.error("❌ Gateway connectivity test failed: {}", e.getMessage());

            result.put("gatewayConnectivity", "FAILED");
            result.put("error", e.getMessage());
            result.put("errorType", e.getClass().getSimpleName());
            result.put("message", "Gateway is NOT reachable - this is the root problem");

            // Información adicional del error
            if (e.getMessage().contains("Connection refused")) {
                result.put("rootCause", "Gateway is not running on port 8080");
                result.put("solution", "Start the Gateway service first");
            }
        }

        result.put("userServiceUrl", userServiceUrl);
        result.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(result);
    }
}