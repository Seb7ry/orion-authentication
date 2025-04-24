package com.unibague.gradework.orionserver.authentication;

import com.unibague.gradework.orionserver.user.models.UserLogDTO;
import com.unibague.gradework.orionserver.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements IAuthService {

    @Autowired
    private IUserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserLogDTO authenticate(LoginRequest loginRequest) {
        UserLogDTO user = userService.getUserByEmail(loginRequest.getEmail());

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        return user;
    }
}
