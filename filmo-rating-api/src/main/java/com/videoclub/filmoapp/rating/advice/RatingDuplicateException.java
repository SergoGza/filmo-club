package com.videoclub.filmoapp.rating.advice;

public class RatingDuplicateException extends RuntimeException {
  public RatingDuplicateException(String message) {
    super(message);
  }
}
