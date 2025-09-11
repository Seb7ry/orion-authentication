package com.unibague.gradework.orionauth.authentication;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Manages RSA key pairs for JWT signing and verification
 * Handles key generation, loading, and persistence
 */
@Slf4j
@Component
public class JwtKeyManager {

    private static final String ALGORITHM = "RSA";
    private static final int KEY_SIZE = 2048;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    /**
     * Initialize keys - either load from files or generate new ones
     */
    public void initializeKeys() {
        try {
            // Try to load existing keys first
            if (loadKeysFromFiles()) {
                log.info("JWT RSA keys loaded from files successfully");
                return;
            }

            // Generate new keys if files don't exist
            generateNewKeyPair();
            log.info("New JWT RSA key pair generated successfully");

        } catch (Exception e) {
            log.error("Failed to initialize JWT keys: {}", e.getMessage());
            throw new RuntimeException("JWT key initialization failed", e);
        }
    }

    /**
     * Generate a new RSA key pair
     */
    private void generateNewKeyPair() throws Exception {
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        keyGenerator.initialize(KEY_SIZE);
        KeyPair keyPair = keyGenerator.generateKeyPair();

        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();

        // Optionally save to files for persistence
        saveKeysToFiles();
    }

    /**
     * Load keys from PEM files
     */
    private boolean loadKeysFromFiles() {
        try {
            String privateKeyPath = System.getenv("JWT_PRIVATE_KEY_PATH");
            String publicKeyPath = System.getenv("JWT_PUBLIC_KEY_PATH");

            if (privateKeyPath == null || publicKeyPath == null) {
                log.debug("JWT key paths not configured, will generate new keys");
                return false;
            }

            if (!Files.exists(Paths.get(privateKeyPath)) || !Files.exists(Paths.get(publicKeyPath))) {
                log.debug("JWT key files not found, will generate new keys");
                return false;
            }

            // Load private key
            String privateKeyContent = Files.readString(Paths.get(privateKeyPath))
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyContent);
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            this.privateKey = KeyFactory.getInstance(ALGORITHM).generatePrivate(privateKeySpec);

            // Load public key
            String publicKeyContent = Files.readString(Paths.get(publicKeyPath))
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyContent);
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            this.publicKey = KeyFactory.getInstance(ALGORITHM).generatePublic(publicKeySpec);

            return true;

        } catch (Exception e) {
            log.warn("Failed to load keys from files: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Save keys to PEM files for persistence
     */
    private void saveKeysToFiles() {
        try {
            String privateKeyPath = System.getenv("JWT_PRIVATE_KEY_PATH");
            String publicKeyPath = System.getenv("JWT_PUBLIC_KEY_PATH");

            if (privateKeyPath != null && publicKeyPath != null) {
                // Create directories if they don't exist
                Files.createDirectories(Paths.get(privateKeyPath).getParent());
                Files.createDirectories(Paths.get(publicKeyPath).getParent());

                // Save private key
                String privateKeyPem = "-----BEGIN PRIVATE KEY-----\n" +
                        Base64.getEncoder().encodeToString(privateKey.getEncoded()) + "\n" +
                        "-----END PRIVATE KEY-----";
                Files.writeString(Paths.get(privateKeyPath), privateKeyPem);

                // Save public key
                String publicKeyPem = "-----BEGIN PUBLIC KEY-----\n" +
                        Base64.getEncoder().encodeToString(publicKey.getEncoded()) + "\n" +
                        "-----END PUBLIC KEY-----";
                Files.writeString(Paths.get(publicKeyPath), publicKeyPem);

                log.info("JWT keys saved to files successfully");
            }
        } catch (Exception e) {
            log.warn("Failed to save keys to files: {}", e.getMessage());
        }
    }

    /**
     * Get the private key for JWT signing
     */
    public PrivateKey getPrivateKey() {
        if (privateKey == null) {
            initializeKeys();
        }
        return privateKey;
    }

    /**
     * Get the public key for JWT verification
     */
    public PublicKey getPublicKey() {
        if (publicKey == null) {
            initializeKeys();
        }
        return publicKey;
    }

    /**
     * Get public key in Base64 format for JWKS endpoint
     */
    public String getPublicKeyBase64() {
        return Base64.getEncoder().encodeToString(getPublicKey().getEncoded());
    }
}