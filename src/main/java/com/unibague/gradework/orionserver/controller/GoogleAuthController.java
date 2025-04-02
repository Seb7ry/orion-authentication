package com.unibague.gradework.orionserver.controller;

import com.unibague.gradework.orionserver.model.User;
import com.unibague.gradework.orionserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth/google")
public class GoogleAuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/callback")
    public ResponseEntity<?> googleCallback(@RequestParam String email) {
        Optional<User> optionalUser = userService.getUserByEmail(email);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El usuario con email " + email + " no está registrado en MongoDB.");
        }
    }
}
