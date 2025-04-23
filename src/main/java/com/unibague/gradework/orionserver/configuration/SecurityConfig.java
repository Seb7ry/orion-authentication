package com.unibague.gradework.orionserver.configuration;

import com.unibague.gradework.orionserver.model.User;
import com.unibague.gradework.orionserver.model.UserLogDTO;
import com.unibague.gradework.orionserver.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Map;

/**
 * Security configuration for the application.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login/oauth2/code/google", "/auth/login").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("http://localhost:5173/director/home", true)
                );
        return http.build();
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService(IUserService userService) {
        return userRequest -> {
            DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
            OAuth2User oAuth2User = delegate.loadUser(userRequest);

            Map<String, Object> attributes = oAuth2User.getAttributes();
            System.out.println("DATOS RECIBIDOS DE GOOGLE:");
            attributes.forEach((key, value) -> System.out.println(key + " = " + value));
            String email = (String) attributes.get("email");

            if (!(email.endsWith("@estudiantesunibague.edu.co") || email.endsWith("@unibague.edu.co"))) {
                throw new RuntimeException("Correo no válido para esta plataforma.");
            }

            try {
                UserLogDTO user = userService.getUserByEmail(email);
                System.out.println("Usuario ya registrado en MongoDB: " + user.getEmail());
            } catch (Exception e) {
                System.out.println("Usuario no encontrado en MongoDB, se debe consultar la API externa.");

            }

            return oAuth2User;
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
