package com.videoclub.filmoapp.security;

import com.videoclub.filmoapp.auth.config.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final CustomUserDetailsService customUserDetailsService;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    return http.securityMatcher("/videoclub/**", "/login", "/logout", "/register")
        .authorizeHttpRequests(
            authz ->
                authz
                    .requestMatchers("/login", "/register", "/videoclub")
                    .permitAll()
                    .requestMatchers("/videoclub/film/films-edit/*")
                    .hasRole("ADMIN")
                    .requestMatchers("/videoclub/film/films-edit")
                    .authenticated()
                    .requestMatchers("/videoclub/**")
                    .authenticated())
        .formLogin(
            loginCustomizer -> loginCustomizer.loginPage("/login").defaultSuccessUrl("/videoclub"))
        .logout(loginCustomizer -> loginCustomizer.logoutUrl("/logout").logoutSuccessUrl("/login"))
        .userDetailsService(customUserDetailsService)
        .build();
  }
}
