package com.videoclub.filmoapp.rating.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@Builder
@Jacksonized
@Value
public class RatingResponseDTO {
  Integer score;
  LocalDateTime createdAt;
}
