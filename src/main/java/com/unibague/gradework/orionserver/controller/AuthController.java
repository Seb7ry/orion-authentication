package com.unibague.gradework.orionserver.controller;

import com.unibague.gradework.orionserver.model.Actor;
import com.unibague.gradework.orionserver.model.LoginRequest;
import com.unibague.gradework.orionserver.model.Student;
import com.unibague.gradework.orionserver.model.User;
import com.unibague.gradework.orionserver.repository.ActorRepository;
import com.unibague.gradework.orionserver.repository.StudentRepository;
import com.unibague.gradework.orionserver.repository.UserRepository;
import com.unibague.gradework.orionserver.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/authenticate")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest) {
        // Buscar en la colección "users"
        Optional<User> user = userRepository.findByEmail(loginRequest.getEmail());

        // Si no se encuentra en la colección "users", buscar en "students"
        if (user.isEmpty()) {
            user = studentRepository.findByEmail(loginRequest.getEmail())
                    .map(student -> (User) student); // Convertir Optional<Student> a Optional<User>
        }

        // Si no se encuentra en "students", buscar en "actors"
        if (user.isEmpty()) {
            user = actorRepository.findByEmail(loginRequest.getEmail())
                    .map(actor -> (User) actor); // Convertir Optional<Actor> a Optional<User>
        }

        // Si sigue vacío, lanzar excepción
        if (user.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }

        // Verificar contraseña
        //if (!passwordEncoder.matches(loginRequest.getPassword(), user.get().getPassword())) {
        //    throw new RuntimeException("Credenciales inválidas");
        //}

        User userEntity = user.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar contraseña sin encriptar (temporal)
        if (!loginRequest.getPassword().equals(userEntity.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        // Generar token
        String token = jwtService.generateToken(user.get().getEmail(), loginRequest.getRole());
        return token;
    }


    @PostMapping("/logout")
    public String logout() {
        SecurityContextHolder.clearContext();
        return "Logout successful";
    }
}
