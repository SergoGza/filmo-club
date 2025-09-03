package com.videoclub.filmoapp.rating.service;

import com.videoclub.filmoapp.rating.dto.AverageRatingResponseDTO;
import com.videoclub.filmoapp.rating.dto.RatingRequestDTO;
import com.videoclub.filmoapp.rating.dto.RatingResponseDTO;

public interface RatingService {

    void createRating(RatingRequestDTO requestDTO);
    RatingResponseDTO getRating(Long filmId, Long userId);
    AverageRatingResponseDTO getAverageRating(Long filmId);

}
