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

    // Excepción para cuando no se encuentra una película
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException ex, Model model) {
        log.error("IllegalArgumentException: {}", ex.getMessage());

        // Si el mensaje contiene "Film", es un error relacionado con películas
        if (ex.getMessage().contains("Film") || ex.getMessage().contains("film")) {
            model.addAttribute("statusCode", 404);
            model.addAttribute("errorTitle", "Película no encontrada");
            model.addAttribute("errorDescription", "La película que buscas no existe en nuestro catálogo.");
            model.addAttribute("actionText", "Ver todas las películas");
            model.addAttribute("actionUrl", "/videoclub/film/films");
        }
        // Si el mensaje contiene "User", es un error relacionado con usuarios
        else if (ex.getMessage().contains("User") || ex.getMessage().contains("user")) {
            model.addAttribute("statusCode", 404);
            model.addAttribute("errorTitle", "Usuario no encontrado");
            model.addAttribute("errorDescription", "El usuario especificado no existe.");
            model.addAttribute("actionText", "Volver al inicio");
            model.addAttribute("actionUrl", "/videoclub");
        }
        // Error genérico
        else {
            model.addAttribute("statusCode", 400);
            model.addAttribute("errorTitle", "Solicitud incorrecta");
            model.addAttribute("errorDescription", ex.getMessage());
            model.addAttribute("actionText", "Volver al inicio");
            model.addAttribute("actionUrl", "/videoclub");
        }

        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("timestamp", LocalDateTime.now());

        return "error/404";
    }

    // Excepción para cuando el estado ya existe (duplicados)
    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalStateException(IllegalStateException ex, Model model) {
        log.error("IllegalStateException: {}", ex.getMessage());

        model.addAttribute("statusCode", 409);
        model.addAttribute("errorTitle", "Conflicto de datos");
        model.addAttribute("errorDescription", ex.getMessage());
        model.addAttribute("actionText", "Volver atrás");
        model.addAttribute("actionUrl", "javascript:history.back()");
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("timestamp", LocalDateTime.now());

        return "error/error";
    }

    // Excepción de acceso denegado (Spring Security)
    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDeniedException(AccessDeniedException ex, Model model) {
        log.error("AccessDeniedException: {}", ex.getMessage());

        model.addAttribute("statusCode", 403);
        model.addAttribute("errorTitle", "Acceso denegado");
        model.addAttribute("errorDescription", "No tienes permisos para realizar esta acción.");
        model.addAttribute("actionText", "Volver al inicio");
        model.addAttribute("actionUrl", "/videoclub");
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("timestamp", LocalDateTime.now());

        return "error/403";
    }

    // Excepción cuando no se encuentra un handler (404)
    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNoHandlerFoundException(NoHandlerFoundException ex, Model model) {
        log.error("NoHandlerFoundException: {}", ex.getMessage());

        model.addAttribute("statusCode", 404);
        model.addAttribute("errorTitle", "Página no encontrada");
        model.addAttribute("errorDescription", "La página que buscas no existe.");
        model.addAttribute("actionText", "Volver al inicio");
        model.addAttribute("actionUrl", "/videoclub");
        model.addAttribute("requestUri", ex.getRequestURL());
        model.addAttribute("timestamp", LocalDateTime.now());

        return "error/404";
    }

    // Excepciones de cliente web (llamadas a API externa)
    @ExceptionHandler(WebClientResponseException.class)
    public String handleWebClientResponseException(WebClientResponseException ex, Model model) {
        log.error("WebClientResponseException: {} - {}", ex.getStatusCode(), ex.getMessage());

        HttpStatus status = (HttpStatus) ex.getStatusCode();

        if (status == HttpStatus.NOT_FOUND) {
            model.addAttribute("statusCode", 404);
            model.addAttribute("errorTitle", "Servicio no disponible");
            model.addAttribute("errorDescription", "El servicio de puntuaciones no está disponible temporalmente.");
        } else if (status.is5xxServerError()) {
            model.addAttribute("statusCode", 503);
            model.addAttribute("errorTitle", "Servicio no disponible");
            model.addAttribute("errorDescription", "Los servicios externos no están disponibles. Inténtalo más tarde.");
        } else {
            model.addAttribute("statusCode", status.value());
            model.addAttribute("errorTitle", "Error de servicio");
            model.addAttribute("errorDescription", "Ha ocurrido un error al comunicarse con los servicios externos.");
        }

        model.addAttribute("actionText", "Volver al inicio");
        model.addAttribute("actionUrl", "/videoclub");
        model.addAttribute("timestamp", LocalDateTime.now());

        return "error/500";
    }

    // Excepción genérica para cualquier otro error no controlado
    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model) {
        log.error("Unhandled exception: ", ex);

        model.addAttribute("statusCode", 500);
        model.addAttribute("errorTitle", "Error inesperado");
        model.addAttribute("errorDescription", "Ha ocurrido un error inesperado. Nuestro equipo ha sido notificado.");
        model.addAttribute("actionText", "Volver al inicio");
        model.addAttribute("actionUrl", "/videoclub");
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("timestamp", LocalDateTime.now());

        return "error/500";
    }
}