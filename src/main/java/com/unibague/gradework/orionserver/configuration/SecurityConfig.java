package com.unibague.gradework.orionserver.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.unibague.gradework.orionserver.api.IApiMapperService;
import com.unibague.gradework.orionserver.api.IApiService;
import com.unibague.gradework.orionserver.user.models.Actor;
import com.unibague.gradework.orionserver.user.models.Student;
import com.unibague.gradework.orionserver.user.models.UserLogDTO;
import com.unibague.gradework.orionserver.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Map;
import java.util.Optional;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final IUserService userService;
    private final IApiService apiService;
    private final IApiMapperService apiMapperService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login/oauth2/code/google", "/auth/login").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("http://localhost:5173/director/home", true)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oauth2UserService()) // Aquí va el método que definimos abajo
                        )
                );

        return http.build();
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        return userRequest -> {
            DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
            OAuth2User oAuth2User = delegate.loadUser(userRequest);

            Map<String, Object> attributes = oAuth2User.getAttributes();
            String email = (String) attributes.get("email");
            String image = (String) attributes.get("picture");

            boolean isStudent = email.endsWith("@estudiantesunibague.edu.co");
            boolean isActor = email.endsWith("@unibague.edu.co");

            System.out.println(">>> AUTENTICADO CON GOOGLE: " + email);

            if (email == null || (!email.endsWith("@estudiantesunibague.edu.co") && !email.endsWith("@unibague.edu.co"))) {
                throw new RuntimeException("Correo no autorizado: " + email);
            }
            try {
                UserLogDTO user = userService.getUserByEmail(email);
                System.out.println(">>> Usuario ya existe en MongoDB: " + user.getEmail());
            } catch (Exception e) {
                System.out.println(">>> Usuario NO encontrado en MongoDB: " + email);
                Optional<JsonNode> userData = isStudent
                        ? apiService.findStudentByEmail(email)
                        : apiService.findActorByEmail(email);
                if (userData.isEmpty()) {
                    throw new RuntimeException("Usuario no encontrado en la API externa: " + email);
                }
                if (isStudent) {
                    Student student = apiMapperService.toStudent(userData.get(), image);
                    userService.createStudent(student);
                } else if (isActor) {
                    Actor actor = apiMapperService.toActor(userData.get(), image);
                    userService.createActor(actor);
                }
                System.out.println(">>> Usuario creado correctamente en Mongo: " + email);
            }

            return oAuth2User;
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            public void addCorsMappings(@NotNull CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:5173")
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowCredentials(true);
            }
        };
    }
}
