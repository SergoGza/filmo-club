package com.videoclub.filmoapp.rating.service.impl;

import com.videoclub.filmoapp.rating.config.JwtConfigurationProperties;
import com.videoclub.filmoapp.rating.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl implements JwtService {

  private final JwtConfigurationProperties jwtConfigurationProperties;
  private final JwtEncoder jwtEncoder;
  private final JwtDecoder jwtDecoder;

  @Override
  public String generateToken(String clientId) {
    JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();

    JwtClaimsSet jwtClaimsSet =
            JwtClaimsSet.builder()
                    .subject(clientId)
                    .issuedAt(Instant.now())
                    .expiresAt(Instant.now().plusMillis(jwtConfigurationProperties.duration().toMillis()))
                    .build();

    Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, jwtClaimsSet));

    return jwt.getTokenValue();
  }

  @Override
  public long getExpirationInSeconds() {
    return jwtConfigurationProperties.duration().toSeconds();
  }

  @Override
  public boolean validateToken(String token) {
    try {
      jwtDecoder.decode(token);
      return true;
    } catch (JwtException e) {
      log.error("Error validating JWT: {}", e.getMessage());
      return false;
    }
  }

  @Override
  public String extractClientId(String token) {
    Jwt jwt = jwtDecoder.decode(token);
    return jwt.getSubject();
  }
}