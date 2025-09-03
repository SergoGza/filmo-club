package com.videoclub.filmoapp.rating.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Builder
@Value
@Jacksonized
public class RatingRequestDTO {

  @NotNull
  Long userId;

  @NotNull
  Long filmId;

  @Min(1)
  @Max(5)
  Integer score;
}
