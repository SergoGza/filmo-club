package com.videoclub.filmoapp.rating.controller;

import com.videoclub.filmoapp.rating.advice.RatingDuplicateException;
import com.videoclub.filmoapp.rating.advice.RatingNotFoundException;
import com.videoclub.filmoapp.rating.advice.UnauthorizedException;
import com.videoclub.filmoapp.rating.dto.AverageRatingResponseDTO;
import com.videoclub.filmoapp.rating.dto.RatingRequestDTO;
import com.videoclub.filmoapp.rating.dto.RatingResponseDTO;
import com.videoclub.filmoapp.rating.service.JwtService;
import com.videoclub.filmoapp.rating.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ratings")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Ratings", description = "Film rating management")
public class RatingController {

  private final RatingService ratingService;
  private final JwtService jwtService;

  @PostConstruct
  public void init() {
    log.info("RatingController initialized - endpoints should be available");
  }

  @PostMapping
  @Operation(
      summary = "Create a new rating",
      description = "Create a new rating for a film. Requires JWT Bearer token.",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponse(responseCode = "201", description = "Rating created successfully")
  @ApiResponse(responseCode = "400", description = "Rating already exists or invalid data")
  @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token")
  public ResponseEntity<?> createRating(
      @Parameter(description = "Bearer JWT token", required = true) @RequestHeader("Authorization")
          String authHeader,
      @Valid @RequestBody RatingRequestDTO requestDTO) {

    try {
      log.debug(
          "Received rating request: userId={}, filmId={}, score={}",
          requestDTO.getUserId(),
          requestDTO.getFilmId(),
          requestDTO.getScore());

      log.debug("Validating token: {}", authHeader);
      validateTokenOrThrow(authHeader);

      ratingService.createRating(requestDTO);

      log.debug(
          "Rating created successfully for film {} by user {}",
          requestDTO.getFilmId(),
          requestDTO.getUserId());

      return ResponseEntity.status(HttpStatus.CREATED).build();

    } catch (RatingDuplicateException e) {
      log.error("Duplicate rating error: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      log.error("Error creating rating", e);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }

  @GetMapping("/films/{filmId}/users/{userId}")
  @Operation(
      summary = "Get user rating for a film",
      description =
          "Get the rating that a specific user gave to a specific film. No authentication required.")
  @ApiResponse(
      responseCode = "200",
      description = "Rating found",
      content = @Content(schema = @Schema(implementation = RatingResponseDTO.class)))
  @ApiResponse(responseCode = "404", description = "Rating not found")
  public ResponseEntity<?> getRating(
      @Parameter(description = "Film ID", required = true) @PathVariable("filmId") Long filmId,
      @Parameter(description = "User ID", required = true) @PathVariable("userId") Long userId) {

    try {
      RatingResponseDTO ratingResponseDTO = ratingService.getRating(filmId, userId);
      return ResponseEntity.ok(ratingResponseDTO);

    } catch (RatingNotFoundException e) {
      return ResponseEntity.notFound().build();
    } catch (Exception e) {
      log.error("Error getting rating", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GetMapping("/average/{filmId}")
  @Operation(
      summary = "Get average rating for a film",
      description =
          "Get the average rating and total number of ratings for a specific film. No authentication required.")
  @ApiResponse(
      responseCode = "200",
      description = "Average rating calculated",
      content = @Content(schema = @Schema(implementation = AverageRatingResponseDTO.class)))
  public ResponseEntity<?> getAverageRating(
      @Parameter(description = "Film ID", required = true) @PathVariable("filmId") Long filmId,
      HttpServletRequest request) {
    Collections.list(request.getHeaderNames()).stream()
        .collect(Collectors.toMap(h -> h, request::getHeader));
    try {
      AverageRatingResponseDTO averageRatingResponseDTO = ratingService.getAverageRating(filmId);
      return ResponseEntity.ok(averageRatingResponseDTO);

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error: " + e.getMessage());
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
