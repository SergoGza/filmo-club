package com.videoclub.filmoapp.rating.advice;

public class RatingNotFoundException extends RuntimeException {
  public RatingNotFoundException(String message) {
    super(message);
  }
}
