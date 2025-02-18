package com.unibague.gradework.orionserver.service;

import com.unibague.gradework.orionserver.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class responsible for handling authentication logic.
 * It verifies user credentials, retrieves user details, and generates JWT tokens.
 */
@Service
public class AuthService implements IAuthService{

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Authenticates a user by validating credentials and generating a JWT token.
     *
     * @param loginRequest The login request containing the user's email and password.
     * @return A {@link ResponseEntity} containing an authentication response if successful,
     * or an error message in case of failure.
     */
    @Override
    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
        Optional<User> userOptional = userService.fetchUserByEmail(loginRequest.getEmail());

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }

        User user = userOptional.get();

        if (!loginRequest.getPassword().equals(user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }

        List<ProgramDTO> programs = userService.fetchUserPrograms(user.getIdUser());

        Object userDTO;
        if (user instanceof Student student) {
            userDTO = new StudentDTO(
                    student.getIdUser(),
                    student.getFirstName(),
                    student.getLastName(),
                    student.getBirthDate(),
                    student.getPhone(),
                    student.getEmail(),
                    student.getImage(),
                    student.getSex().name(),
                    student.getRole().getName(),
                    programs,
                    String.valueOf(student.getStudentID()),
                    student.isStatus(),
                    student.getSemester(),
                    student.getCategory()
            );
        } else if (user instanceof Actor actor) {
            userDTO = new ActorDTO(
                    actor.getIdUser(),
                    actor.getFirstName(),
                    actor.getLastName(),
                    actor.getBirthDate(),
                    actor.getPhone(),
                    actor.getEmail(),
                    actor.getImage(),
                    actor.getSex().name(),
                    actor.getRole().getName(),
                    programs,
                    String.valueOf(actor.getEmployeeId()),
                    actor.getPosition()
            );
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("User type not recognized");
        }

        String token = jwtService.generateToken(user);

        AuthResponse response = AuthResponse.builder()
                .token(token)
                .user(userDTO)
                .build();

        return ResponseEntity.ok(response);
    }
}
