package com.videoclub.filmoapp.rating.service.impl;

import com.videoclub.filmoapp.rating.advice.InvalidGrantTypeException;
import com.videoclub.filmoapp.rating.advice.UnauthorizedException;
import com.videoclub.filmoapp.rating.config.JwtConfigurationProperties;
import com.videoclub.filmoapp.rating.dto.AuthenticationResponseDTO;
import com.videoclub.filmoapp.rating.service.AuthService;
import com.videoclub.filmoapp.rating.service.JwtService;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final JwtService jwtService;
  private final JwtConfigurationProperties jwtConfigurationProperties;

  @Override
  public AuthenticationResponseDTO authenticate(String grantType, String authHeader) {


      // 1. Validar grant_type

      if (!"client_credentials".equals(grantType)) {
        throw new InvalidGrantTypeException("Invalid grant type");
      }

      // 2. Extraer credenciales de Basic Auth

      if (authHeader == null || authHeader.isEmpty() || !authHeader.startsWith("Basic ")) {
        throw new UnauthorizedException("invalid auth_header");
      }

      String base64Credentials = authHeader.substring("Basic ".length());
      String credentials =
          new String(Base64.getDecoder().decode(base64Credentials), StandardCharsets.UTF_8);
      String[] values = credentials.split(":", 2);

      if (values.length != 2) {
        throw new UnauthorizedException("invalid auth_header");
      }

      String clientId = values[0];
      String clientSecret = values[1];

      // 3. Validar credenciales contra lo que tengas en config (ejemplo simple)

      JwtConfigurationProperties.OAuthProperties oauth = jwtConfigurationProperties.oauth();

      if (!oauth.clientId().equals(clientId) || !oauth.clientSecret().equals(clientSecret)) {
        throw new UnauthorizedException("invalid auth_header");
      }

      // Generar token

      String token = jwtService.generateToken(clientId);

      // Responder como haría un Authorization server


        return AuthenticationResponseDTO.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationInSeconds())
                .build();
    }
  }

