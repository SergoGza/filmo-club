package com.videoclub.filmoapp.web.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException ex, Model model) {
        log.error("IllegalArgumentException: {}", ex.getMessage());

        if (ex.getMessage().contains("Film") || ex.getMessage().contains("film")) {
            model.addAttribute("statusCode", 404);
            model.addAttribute("errorTitle", "Film Not Found");
            model.addAttribute("errorDescription", "The film you're looking for doesn't exist in our catalog.");
            model.addAttribute("actionText", "View All Films");
            model.addAttribute("actionUrl", "/videoclub/film/films");
        }
        else if (ex.getMessage().contains("User") || ex.getMessage().contains("user")) {
            model.addAttribute("statusCode", 404);
            model.addAttribute("errorTitle", "User Not Found");
            model.addAttribute("errorDescription", "The specified user doesn't exist.");
            model.addAttribute("actionText", "Back to Home");
            model.addAttribute("actionUrl", "/videoclub");
        }
        else {
            model.addAttribute("statusCode", 400);
            model.addAttribute("errorTitle", "Bad Request");
            model.addAttribute("errorDescription", ex.getMessage());
            model.addAttribute("actionText", "Back to Home");
            model.addAttribute("actionUrl", "/videoclub");
        }

        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("timestamp", LocalDateTime.now());

        return "error/404";
    }

    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalStateException(IllegalStateException ex, Model model) {
        log.error("IllegalStateException: {}", ex.getMessage());

        model.addAttribute("statusCode", 409);
        model.addAttribute("errorTitle", "Data Conflict");
        model.addAttribute("errorDescription", ex.getMessage());
        model.addAttribute("actionText", "Go Back");
        model.addAttribute("actionUrl", "javascript:history.back()");
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("timestamp", LocalDateTime.now());

        return "error/error";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDeniedException(AccessDeniedException ex, Model model) {
        log.error("AccessDeniedException: {}", ex.getMessage());

        model.addAttribute("statusCode", 403);
        model.addAttribute("errorTitle", "Access Denied");
        model.addAttribute("errorDescription", "You don't have permission to perform this action.");
        model.addAttribute("actionText", "Back to Home");
        model.addAttribute("actionUrl", "/videoclub");
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("timestamp", LocalDateTime.now());

        return "error/403";
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNoHandlerFoundException(NoHandlerFoundException ex, Model model) {
        log.error("NoHandlerFoundException: {}", ex.getMessage());

        model.addAttribute("statusCode", 404);
        model.addAttribute("errorTitle", "Page Not Found");
        model.addAttribute("errorDescription", "The page you're looking for doesn't exist.");
        model.addAttribute("actionText", "Back to Home");
        model.addAttribute("actionUrl", "/videoclub");
        model.addAttribute("requestUri", ex.getRequestURL());
        model.addAttribute("timestamp", LocalDateTime.now());

        return "error/404";
    }

    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    public String handleNoResourceFoundException(org.springframework.web.servlet.resource.NoResourceFoundException ex, Model model) {
        log.error("NoResourceFoundException: {}", ex.getMessage());

        model.addAttribute("statusCode", 404);
        model.addAttribute("errorTitle", "Page Not Found");
        model.addAttribute("errorDescription", "The page you're looking for doesn't exist.");
        model.addAttribute("actionText", "Back to Home");
        model.addAttribute("actionUrl", "/videoclub");
        model.addAttribute("requestUri", ex.getResourcePath());
        model.addAttribute("timestamp", LocalDateTime.now());

        return "error/404";
    }

    @ExceptionHandler(WebClientResponseException.class)
    public String handleWebClientResponseException(WebClientResponseException ex, Model model) {
        log.error("WebClientResponseException: {} - {}", ex.getStatusCode(), ex.getMessage());

        HttpStatus status = (HttpStatus) ex.getStatusCode();

        if (status == HttpStatus.NOT_FOUND) {
            model.addAttribute("statusCode", 404);
            model.addAttribute("errorTitle", "Service Unavailable");
            model.addAttribute("errorDescription", "The rating service is temporarily unavailable.");
        } else if (status.is5xxServerError()) {
            model.addAttribute("statusCode", 503);
            model.addAttribute("errorTitle", "Service Unavailable");
            model.addAttribute("errorDescription", "External services are unavailable. Please try again later.");
        } else {
            model.addAttribute("statusCode", status.value());
            model.addAttribute("errorTitle", "Service Error");
            model.addAttribute("errorDescription", "An error occurred while communicating with external services.");
        }

        model.addAttribute("actionText", "Back to Home");
        model.addAttribute("actionUrl", "/videoclub");
        model.addAttribute("timestamp", LocalDateTime.now());

        return "error/500";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model) {
        log.error("Unhandled exception: ", ex);

        model.addAttribute("statusCode", 500);
        model.addAttribute("errorTitle", "Unexpected Error");
        model.addAttribute("errorDescription", "An unexpected error occurred. Our team has been notified.");
        model.addAttribute("actionText", "Back to Home");
        model.addAttribute("actionUrl", "/videoclub");
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("timestamp", LocalDateTime.now());

        return "error/500";
    }
}