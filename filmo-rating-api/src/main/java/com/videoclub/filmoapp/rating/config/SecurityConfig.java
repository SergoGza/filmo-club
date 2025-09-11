package com.videoclub.filmoapp.rating.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults()) // ← Añadir CORS
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Swagger/OpenAPI endpoints - públicos
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Endpoint de autenticación - público
                        .requestMatchers(HttpMethod.POST, "/authenticate").permitAll()
                        .requestMatchers(HttpMethod.GET, "/authenticate/debug").permitAll()

                        // Endpoints de lectura - públicos
                        .requestMatchers(HttpMethod.GET, "/ratings/average/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/ratings/films/**").permitAll()

                        // Endpoints de escritura - requieren JWT
                        .requestMatchers(HttpMethod.POST, "/ratings").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/ratings/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/ratings/**").authenticated()

                        .anyRequest().permitAll()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .build();
    }
}