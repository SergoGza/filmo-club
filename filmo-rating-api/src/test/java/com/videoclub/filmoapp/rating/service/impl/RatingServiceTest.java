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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        properties = {
                "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MYSQL;DATABASE_TO_LOWER=TRUE",
                "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
                "spring.jpa.hibernate.ddl-auto=create-drop"
        })
@Transactional
class RatingServiceTest {

    @Autowired
    private RatingService ratingService;

    @Autowired
    private RatingDAO ratingDAO;

    private Rating existingRating;

    @BeforeEach
    void setUp() {
        // Create some test ratings
        existingRating = ratingDAO.save(Rating.builder()
                .filmId(1L)
                .userId(1L)
                .score(5)
                .build());

        ratingDAO.save(Rating.builder()
                .filmId(1L)
                .userId(2L)
                .score(4)
                .build());

        ratingDAO.save(Rating.builder()
                .filmId(1L)
                .userId(3L)
                .score(3)
                .build());

        ratingDAO.save(Rating.builder()
                .filmId(2L)
                .userId(1L)
                .score(5)
                .build());
    }

    @Test
    @DisplayName("Should create new rating successfully")
    void createRating_Success() {
        // Given
        RatingRequestDTO requestDTO = RatingRequestDTO.builder()
                .filmId(3L)
                .userId(1L)
                .score(4)
                .build();

        // When
        assertDoesNotThrow(() -> ratingService.createRating(requestDTO));

        // Then
        List<Rating> ratings = ratingDAO.findByFilmId(3L);
        assertEquals(1, ratings.size());
        assertEquals(4, ratings.get(0).getScore());
        assertEquals(1L, ratings.get(0).getUserId());
    }

    @Test
    @DisplayName("Should throw exception when rating already exists")
    void createRating_DuplicateRating() {
        // Given - Rating already exists for film 1, user 1
        RatingRequestDTO requestDTO = RatingRequestDTO.builder()
                .filmId(1L)
                .userId(1L)
                .score(3)
                .build();

        // When & Then
        RatingDuplicateException exception = assertThrows(
                RatingDuplicateException.class,
                () -> ratingService.createRating(requestDTO)
        );
        assertEquals("Rating already exists", exception.getMessage());
    }

    @Test
    @DisplayName("Should get existing rating successfully")
    void getRating_Success() {
        // When
        RatingResponseDTO result = ratingService.getRating(1L, 1L);

        // Then
        assertNotNull(result);
        assertEquals(5, result.getScore());
        assertNotNull(result.getCreatedAt());
    }

    @Test
    @DisplayName("Should throw exception when rating not found")
    void getRating_NotFound() {
        // When & Then
        RatingNotFoundException exception = assertThrows(
                RatingNotFoundException.class,
                () -> ratingService.getRating(999L, 999L)
        );
        assertEquals("Rating not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should calculate average rating correctly")
    void getAverageRating_WithRatings() {
        // When - Film 1 has ratings: 5, 4, 3
        AverageRatingResponseDTO result = ratingService.getAverageRating(1L);

        // Then
        assertNotNull(result);
        assertEquals(4.0, result.getAverage()); // (5+4+3)/3 = 4.0
        assertEquals(3L, result.getRatings());
    }

    @Test
    @DisplayName("Should return zero average when no ratings exist")
    void getAverageRating_NoRatings() {
        // When - Film 999 has no ratings
        AverageRatingResponseDTO result = ratingService.getAverageRating(999L);

        // Then
        assertNotNull(result);
        assertEquals(0.0, result.getAverage());
        assertEquals(0L, result.getRatings());
    }

    @Test
    @DisplayName("Should round average rating correctly")
    void getAverageRating_RoundingUp() {
        // Given - Add more ratings to test rounding
        ratingDAO.save(Rating.builder()
                .filmId(5L)
                .userId(1L)
                .score(5)
                .build());

        ratingDAO.save(Rating.builder()
                .filmId(5L)
                .userId(2L)
                .score(4)
                .build());

        ratingDAO.save(Rating.builder()
                .filmId(5L)
                .userId(3L)
                .score(4)
                .build());

        // When - Average should be 4.333... which rounds up to 4.34
        AverageRatingResponseDTO result = ratingService.getAverageRating(5L);

        // Then
        assertNotNull(result);
        assertEquals(4.34, result.getAverage(), 0.01);
        assertEquals(3L, result.getRatings());
    }

    @Test
    @DisplayName("Should validate score range (1-5)")
    void createRating_ValidateScoreRange() {
        // Test minimum score (1)
        RatingRequestDTO minScoreRequest = RatingRequestDTO.builder()
                .filmId(10L)
                .userId(10L)
                .score(1)
                .build();

        assertDoesNotThrow(() -> ratingService.createRating(minScoreRequest));

        // Test maximum score (5)
        RatingRequestDTO maxScoreRequest = RatingRequestDTO.builder()
                .filmId(11L)
                .userId(11L)
                .score(5)
                .build();

        assertDoesNotThrow(() -> ratingService.createRating(maxScoreRequest));
    }

    @Test
    @DisplayName("Should handle multiple ratings for same film")
    void getAverageRating_MultipleRatings() {
        // Given - Add many ratings for film 10
        for (long userId = 1; userId <= 10; userId++) {
            ratingDAO.save(Rating.builder()
                    .filmId(10L)
                    .userId(userId)
                    .score((int) ((userId % 5) + 1)) // Scores from 1 to 5
                    .build());
        }

        // When
        AverageRatingResponseDTO result = ratingService.getAverageRating(10L);

        // Then
        assertNotNull(result);
        assertEquals(10L, result.getRatings());
        assertTrue(result.getAverage() > 0);
    }

    @Test
    @DisplayName("Should get ratings by film ID")
    void getRatingsByFilmId() {
        // When
        List<Rating> ratings = ratingDAO.findByFilmId(1L);

        // Then
        assertNotNull(ratings);
        assertEquals(3, ratings.size()); // Film 1 has 3 ratings in setUp
    }

    @Test
    @DisplayName("Should check if rating exists for user and film")
    void existsByFilmIdAndUserId() {
        // When & Then
        assertTrue(ratingDAO.existsByFilmIdAndUserId(1L, 1L));
        assertFalse(ratingDAO.existsByFilmIdAndUserId(999L, 999L));
    }

    @Test
    @DisplayName("Should maintain unique constraint on film-user combination")
    void uniqueConstraint_FilmUser() {
        // Given
        Rating rating1 = Rating.builder()
                .filmId(100L)
                .userId(100L)
                .score(5)
                .build();

        // When
        ratingDAO.save(rating1);

        // Then - Try to save duplicate
        Rating rating2 = Rating.builder()
                .filmId(100L)
                .userId(100L)
                .score(3)
                .build();

        assertThrows(Exception.class, () -> {
            ratingDAO.saveAndFlush(rating2);
        });
    }

    @Test
    @DisplayName("Should have createdAt timestamp automatically set")
    void rating_CreatedAtTimestamp() {
        // Given
        LocalDateTime beforeSave = LocalDateTime.now();

        Rating newRating = Rating.builder()
                .filmId(200L)
                .userId(200L)
                .score(4)
                .build();

        // When
        Rating savedRating = ratingDAO.save(newRating);
        ratingDAO.flush();

        // Then
        assertNotNull(savedRating.getCreatedAt());
        assertTrue(savedRating.getCreatedAt().isAfter(beforeSave) ||
                savedRating.getCreatedAt().isEqual(beforeSave));
    }
}