package com.videoclub.filmoapp.rating.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Builder
@Value
@Jacksonized
public class AverageRatingResponseDTO {

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
  Double average;
  Long ratings;
}
