package com.unibague.gradework.orionserver.Auth0;

import com.unibague.gradework.orionserver.JWT.JwtService;
import com.unibague.gradework.orionserver.enumerator.Role;
import com.unibague.gradework.orionserver.model.User;
import com.unibague.gradework.orionserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService
{
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthResponse login(LoginRequest request)
    {
        return null;
    }

    public AuthResponse register(RegisterRequest request)
    {
        User user = User.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .image(request.getImage())
                .sex(request.getSex())
                .password(request.getPassword())
                .role(Role.USER)
                .build();

        userRepository.save(user);

        return AuthResponse.builder()
                .token(jwtService.getToken(user))
                .build();
    }
}
