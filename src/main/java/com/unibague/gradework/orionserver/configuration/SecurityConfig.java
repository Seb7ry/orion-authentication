package com.unibague.gradework.orionserver.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.unibague.gradework.orionserver.api.IApiMapperService;
import com.unibague.gradework.orionserver.api.IApiService;
import com.unibague.gradework.orionserver.program.Program;
import com.unibague.gradework.orionserver.user.models.*;
import com.unibague.gradework.orionserver.user.IUserService;
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

import java.util.*;

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
                .cors(Customizer.withDefaults()) // 👈 habilita CORS correctamente
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login/oauth2/code/google", "/auth/login").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("http://localhost:5173/auth/google/callback", true)
                        .userInfoEndpoint(userInfo -> userInfo.userService(oauth2UserService()))
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

            if (email == null || (!email.endsWith("@estudiantesunibague.edu.co") && !email.endsWith("@unibague.edu.co"))) {
                throw new RuntimeException("Correo no autorizado: " + email);
            }

            try {
                userService.getUserByEmail(email);
                System.out.println(">>> Usuario ya existe en MongoDB: " + email);
            } catch (Exception e) {
                System.out.println(">>> Usuario NO encontrado en MongoDB: " + email);
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

                System.out.println(">>> Usuario creado correctamente en Mongo: " + email);
            }

            UserLogDTO userMongo = userService.getUserByEmail(email);

// Imprimir bonito
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                String jsonPretty = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(userMongo);
                System.out.println(">>> Objeto que se enviará al frontend:");
                System.out.println(jsonPretty);
            } catch (Exception ex) {
                System.out.println(">>> Error imprimiendo JSON bonito: " + ex.getMessage());
                System.out.println(userMongo);
            }

// Crear atributos
            Map<String, Object> simpleAttributes = new HashMap<>();
            simpleAttributes.put("idUser", userMongo.getIdUser());
            simpleAttributes.put("email", userMongo.getEmail());
            simpleAttributes.put("name", userMongo.getName());
            simpleAttributes.put("phone", userMongo.getPhone());
            simpleAttributes.put("image", userMongo.getImage());
            simpleAttributes.put("sex", userMongo.getSex());
            simpleAttributes.put("role", userMongo.getRole() != null ? userMongo.getRole().getName() : null);

// 🔥 Programas simplificados
            List<Map<String, Object>> programList = new ArrayList<>();
            if (userMongo.getPrograms() != null) {
                for (Program program : userMongo.getPrograms()) {
                    Map<String, Object> programData = new HashMap<>();
                    programData.put("programId", program.getProgramId());
                    programData.put("programName", program.getProgramName());
                    programList.add(programData);
                }
            }
            simpleAttributes.put("programs", programList);

// 🔥 Si es estudiante o actor
            if (userMongo instanceof StudentLogDTO student) {
                simpleAttributes.put("studentID", student.getStudentID());
                simpleAttributes.put("semester", student.getSemester());
                simpleAttributes.put("status", student.isStatus());
            } else if (userMongo instanceof ActorLogDTO actor) {
                simpleAttributes.put("position", actor.getPosition());
            }

            return new DefaultOAuth2User(
                    oAuth2User.getAuthorities(),
                    simpleAttributes,
                    "email"
            );

        };
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
