package com.unibague.gradework.orionserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unibague.gradework.orionserver.model.LoginRequest;
import com.unibague.gradework.orionserver.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class AuthService implements IAuthService {

    @Autowired
    private IUserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     @Override
     public User authenticate(LoginRequest loginRequest) {
         User user = userService.getUserByEmail(loginRequest.getEmail());

         boolean matches = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
         if (!matches) {
            throw new IllegalArgumentException("Credenciales inválidas");
         }

         return user;
     }
    }*/

    @Override
    public User authenticate(LoginRequest loginRequest) {
        User user = userService.getUserByEmail(loginRequest.getEmail());

        if (!loginRequest.getPassword().equals(user.getPassword())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        return user;
    }
}
