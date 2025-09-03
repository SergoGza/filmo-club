package com.videoclub.filmoapp.rating.controller;

import com.videoclub.filmoapp.rating.advice.RatingDuplicateException;
import com.videoclub.filmoapp.rating.advice.RatingNotFoundException;
import com.videoclub.filmoapp.rating.advice.UnauthorizedException;
import com.videoclub.filmoapp.rating.dto.AverageRatingResponseDTO;
import com.videoclub.filmoapp.rating.dto.RatingRequestDTO;
import com.videoclub.filmoapp.rating.dto.RatingResponseDTO;
import com.videoclub.filmoapp.rating.service.JwtService;
import com.videoclub.filmoapp.rating.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ratings")
@RequiredArgsConstructor
public class RatingController {

  private final RatingService ratingService;
  private final JwtService jwtService;

  @PostMapping
  public ResponseEntity<?> createRating(
      @RequestHeader("Authorization") String authHeader,
      @Valid @RequestBody RatingRequestDTO requestDTO) {

    try {

      validateTokenOrThrow(authHeader);
      ratingService.createRating(requestDTO);
      return ResponseEntity.status(HttpStatus.CREATED).build();

    } catch (RatingDuplicateException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }



  @GetMapping("/films/{filmId}/users/{userId}")
  public ResponseEntity<?> getRating(
      @RequestHeader("Authorization") String authHeader,
      @PathVariable Long filmId,
      @PathVariable Long userId) {

    try {
      validateTokenOrThrow(authHeader);
      RatingResponseDTO ratingResponseDTO = ratingService.getRating(filmId, userId);
      return ResponseEntity.ok(ratingResponseDTO);

    } catch (RatingNotFoundException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }

  @GetMapping("-average/films/{filmId}")
  public ResponseEntity<?> getAverageRating(
      @RequestHeader("Authorization") String authHeader, @PathVariable Long filmId) {

    try {
      validateTokenOrThrow(authHeader);
      AverageRatingResponseDTO averageRatingResponseDTO = ratingService.getAverageRating(filmId);
      return ResponseEntity.ok(averageRatingResponseDTO);

    } catch (RatingDuplicateException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }

  private String extractTokenFromHeader(String authHeader) {
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      return authHeader.substring(7);
    }
    throw new IllegalArgumentException("Invalid auth header");
  }

  private void validateTokenOrThrow(String authHeader) {
    String token = extractTokenFromHeader(authHeader);
    if (!jwtService.validateToken(token)) {
      throw new UnauthorizedException("Token inválido o expirado");
    }
  }

}
