package com.videoclub.filmoapp.rating.service.impl;

import com.videoclub.filmoapp.rating.advice.RatingDuplicateException;
import com.videoclub.filmoapp.rating.advice.RatingNotFoundException;
import com.videoclub.filmoapp.rating.domain.Rating;
import com.videoclub.filmoapp.rating.dto.AverageRatingResponseDTO;
import com.videoclub.filmoapp.rating.dto.RatingRequestDTO;
import com.videoclub.filmoapp.rating.dto.RatingResponseDTO;
import com.videoclub.filmoapp.rating.repository.RatingDAO;
import com.videoclub.filmoapp.rating.service.RatingService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

  private final RatingDAO ratingDAO;

  @Transactional
  @Override
  public void createRating(RatingRequestDTO requestDTO) {


    if (ratingDAO.existsByFilmIdAndUserId(requestDTO.getFilmId(), requestDTO.getUserId())) {
      throw new RatingDuplicateException("Rating already exists");
    }

    Rating rating =
        Rating.builder()
            .filmId(requestDTO.getFilmId())
            .userId(requestDTO.getUserId())
            .score(requestDTO.getScore())
            .build();

    ratingDAO.save(rating);


  }

  @Override
  public RatingResponseDTO getRating(Long filmId, Long userId) {

    //    Buscar en BD por filmId + userId

    Optional<Rating> maybeRating = ratingDAO.findByFilmIdAndUserId(filmId, userId);

    if (maybeRating.isEmpty()) {
      throw new RatingNotFoundException("Rating not found");
    }

    return RatingResponseDTO.builder()
        .createdAt(maybeRating.get().getCreatedAt())
        .score(maybeRating.get().getScore())
        .build();
  }

  @Override
  public AverageRatingResponseDTO getAverageRating(Long filmId) {

    List<Rating> ratings = ratingDAO.findByFilmId(filmId);
    // Si no hay ratings
    if (ratings.isEmpty()) {
      return AverageRatingResponseDTO.builder()
              .average(0.0)
              .ratings(0L)
              .build();
    }

    //Calcular el average

    double rawAverage = ratings.stream()
            .mapToInt(Rating::getScore)
            .average()
            .orElse(0.0);

    double roundedUp = Math.ceil(rawAverage * 100.0) / 100.0;

    return AverageRatingResponseDTO.builder()
            .average(roundedUp)
            .ratings((long)ratings.size())
            .build();
  }
}
