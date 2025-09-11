package com.unibague.gradework.orionauth.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unibague.gradework.orionauth.api.IApiMapperService;
import com.unibague.gradework.orionauth.api.IApiService;
import com.unibague.gradework.orionauth.authentication.UserResponseBuilder;
import com.unibague.gradework.orionauth.user.models.*;
import com.unibague.gradework.orionauth.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;
import java.util.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final IUserService userService;
    private final IApiService apiService;
    private final IApiMapperService apiMapperService;
    private final UserResponseBuilder userResponseBuilder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login/oauth2/code/google", "/auth/login").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler((request, response, authentication) -> {
                            // Redirigir siempre a Vue con el flag para que consulte /auth/me
                            response.sendRedirect("http://localhost:5173/oauth-loading");
                        })
                        .userInfoEndpoint(userInfo -> userInfo.userService(oauth2UserService()))
                );

        return http.build();
    }
    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        return userRequest -> {
            DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
            OAuth2User oAuth2User = delegate.loadUser(userRequest);

            String email = (String) oAuth2User.getAttributes().get("email");
            String image = (String) oAuth2User.getAttributes().get("picture");

            if (email == null || (!email.endsWith("@estudiantesunibague.edu.co") && !email.endsWith("@unibague.edu.co"))) {
                throw new RuntimeException("Correo no autorizado: " + email);
            }

            try {
                userService.getUserByEmail(email);
            } catch (Exception e) {
                Optional<JsonNode> userData = email.endsWith("@estudiantesunibague.edu.co")
                        ? apiService.findStudentByEmail(email)
                        : apiService.findActorByEmail(email);

                if (userData.isEmpty()) {
                    throw new RuntimeException("Usuario no encontrado en la API externa: " + email);
                }

                if (email.endsWith("@estudiantesunibague.edu.co")) {
                    Student student = apiMapperService.toStudent(userData.get(), image);
                    userService.createStudent(student);
                } else {
                    Actor actor = apiMapperService.toActor(userData.get(), image);
                    userService.createActor(actor);
                }
            }

            UserLogDTO userMongo = userService.getUserByEmail(email);
            Map<String, Object> response = userResponseBuilder.buildUserResponse(userMongo);

            System.out.println(">>> Atributos que se enviarán al frontend (OAuth):");
            try {
                new ObjectMapper()
                        .writerWithDefaultPrettyPrinter()
                        .writeValue(System.out, response);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return new DefaultOAuth2User(oAuth2User.getAuthorities(), response, "email");
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
