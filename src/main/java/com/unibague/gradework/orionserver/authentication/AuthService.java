package com.unibague.gradework.orionserver.authentication;

import com.unibague.gradework.orionserver.user.models.UserLogDTO;
import com.unibague.gradework.orionserver.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService implements IAuthService {

    @Autowired private IUserService userService;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private UserResponseBuilder userResponseBuilder;

    @Autowired private JwtUtil jwtUtil;


    @Override
    public Map<String, Object> authenticate(LoginRequest loginRequest) {
        UserLogDTO user = userService.getUserByEmail(loginRequest.getEmail());

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        Map<String, Object> response = userResponseBuilder.buildUserResponse(user);

        String token = jwtUtil.generateToken(user.getEmail());
        response.put("token", token);

        return response;
    }

}
