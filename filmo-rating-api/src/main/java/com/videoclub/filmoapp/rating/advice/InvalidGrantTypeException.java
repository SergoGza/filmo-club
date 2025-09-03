package com.videoclub.filmoapp.rating.advice;

public class InvalidGrantTypeException extends RuntimeException {
    public InvalidGrantTypeException(String message) {
        super(message);
    }
}