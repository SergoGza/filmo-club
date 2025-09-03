package com.videoclub.filmoapp.rating.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "application.jwt")
public record JwtConfigurationProperties(String secret, Duration duration, OAuthProperties oauth) {

  public record OAuthProperties(String clientId, String clientSecret) {}
}
