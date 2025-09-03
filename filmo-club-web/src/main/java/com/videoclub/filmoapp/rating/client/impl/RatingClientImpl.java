package com.videoclub.filmoapp.rating.client.impl;

import com.videoclub.filmoapp.auth.config.RatingApiConfigurationProperties;
import com.videoclub.filmoapp.rating.client.RatingClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingClientImpl implements RatingClient {

  private final WebClient ratingApiClient;
  private final RatingApiConfigurationProperties properties;

  @Override
  public String getAccessToken() {
    String credentials = properties.oauth().clientId() + ":" + properties.oauth().clientSecret();
    String basicAuth = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());

    TokenResponseDTO tokenResponseDTO =
        ratingApiClient
            .post()
            .uri("/authenticate")
            .header("Authorization", basicAuth)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .bodyValue("grant_type=client_credentials")
            .retrieve()
            .bodyToMono(TokenResponseDTO.class)
            .block();

    return tokenResponseDTO.accessToken();
  }

  @Override
  public void createRating(Long filmId, Long userId, Integer score) {

    String accessToken = getAccessToken();

    ratingApiClient
        .post()
        .uri("/ratings")
        .header("Authorization", "Bearer" + accessToken)
        .bodyValue(new CreateRatingRequestDTO(userId, filmId, score))
        .retrieve()
        .bodyToMono(Void.class)
        .block();
  }

  @Override
  public Integer getUserRating(Long filmId, Long userId) {
    String accessToken = getAccessToken();

    try {
      RatingResponseDTO ratingresponseDTO =
          ratingApiClient
              .get()
              .uri("/ratings/films/{filmId}/users/{userId}", filmId, userId)
              .headers(headers -> headers.setBearerAuth(accessToken))
              .retrieve()
              .bodyToMono(RatingResponseDTO.class)
              .block();

      return ratingresponseDTO.score;
    } catch (WebClientResponseException.NotFound e) {
      return null;
    }
  }

  @Override
  public AverageRatingResponseDTO getAverageRating(Long filmId) {

    String accessToken = getAccessToken();


      AverageRatingResponseDTO averageRatingResponseDTO =
          ratingApiClient
              .get()
              .uri("/ratings-average/films/{filmId}", filmId)
              .headers(headers -> headers.setBearerAuth(accessToken))
              .retrieve()
              .bodyToMono(AverageRatingResponseDTO.class)
              .block();

      return averageRatingResponseDTO;


  }

  public record TokenResponseDTO(String accessToken, String tokenType, Integer expiresIn) {}

  public record CreateRatingRequestDTO(Long userId, Long filmId, Integer score) {}

  public record RatingResponseDTO(Integer score, LocalDateTime createdAt) {}

  public record AverageRatingResponseDTO(Double average, Long ratings) {}
}
