package com.videoclub.filmoapp.rating.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;

public interface JwtService {

  String generateToken(String clientId);

  boolean validateToken(String token);

  String extractClientId(String token);

  long getExpirationInSeconds();
}
