package com.videoclub.filmoapp.auth.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rating-api")
public record RatingApiConfigurationProperties(String urlbase, Duration timeout, OAuthProperties oauth) {

  public record OAuthProperties(String clientId, String clientSecret) {}
}
