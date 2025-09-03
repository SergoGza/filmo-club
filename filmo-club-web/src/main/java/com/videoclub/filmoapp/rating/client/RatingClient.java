package com.videoclub.filmoapp.rating.client;

import com.videoclub.filmoapp.rating.client.impl.RatingClientImpl;

public interface RatingClient {

  String getAccessToken();

  void createRating(Long filmId, Long userId, Integer score);

  Integer getUserRating(Long filmId, Long userId);

  RatingClientImpl.AverageRatingResponseDTO getAverageRating(Long filmId);
}
