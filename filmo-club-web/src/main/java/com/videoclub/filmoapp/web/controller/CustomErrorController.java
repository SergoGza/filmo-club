package com.videoclub.filmoapp.web.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {

        // Obtener información del error
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object errorMessage = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        String requestUri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

        Integer statusCode = null;
        if (status != null) {
            statusCode = Integer.valueOf(status.toString());
        }

        // Log del error para debugging
        log.error("Error {} en {}: {}", statusCode, requestUri, errorMessage);
        if (exception != null) {
            log.error("Exception: ", (Throwable) exception);
        }

        // Añadir información al modelo
        model.addAttribute("statusCode", statusCode);
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("requestUri", requestUri);
        model.addAttribute("timestamp", java.time.LocalDateTime.now());

        // Determinar qué página mostrar
        if (statusCode != null) {
            switch (statusCode) {
                case 401:
                    model.addAttribute("errorTitle", "No autorizado");
                    model.addAttribute("errorDescription", "Necesitas iniciar sesión para acceder a esta página.");
                    model.addAttribute("actionText", "Ir al Login");
                    model.addAttribute("actionUrl", "/login");
                    return "error/401";

                case 403:
                    model.addAttribute("errorTitle", "Acceso denegado");
                    model.addAttribute("errorDescription", "No tienes permisos para acceder a este recurso.");
                    model.addAttribute("actionText", "Volver al inicio");
                    model.addAttribute("actionUrl", "/videoclub");
                    return "error/403";

                case 404:
                    model.addAttribute("errorTitle", "Página no encontrada");
                    model.addAttribute("errorDescription", "La página que buscas no existe o ha sido movida.");
                    model.addAttribute("actionText", "Volver al inicio");
                    model.addAttribute("actionUrl", "/videoclub");
                    return "error/404";

                case 500:
                    model.addAttribute("errorTitle", "Error interno del servidor");
                    model.addAttribute("errorDescription", "Ha ocurrido un error inesperado. Inténtalo de nuevo más tarde.");
                    model.addAttribute("actionText", "Volver al inicio");
                    model.addAttribute("actionUrl", "/videoclub");
                    return "error/500";

                default:
                    if (statusCode >= 400 && statusCode < 500) {
                        model.addAttribute("errorTitle", "Error de solicitud");
                        model.addAttribute("errorDescription", "Ha ocurrido un error con tu solicitud.");
                        return "error/4xx";
                    } else if (statusCode >= 500) {
                        model.addAttribute("errorTitle", "Error del servidor");
                        model.addAttribute("errorDescription", "Ha ocurrido un error interno del servidor.");
                        return "error/5xx";
                    }
            }
        }

        // Error genérico
        model.addAttribute("errorTitle", "Error");
        model.addAttribute("errorDescription", "Ha ocurrido un error inesperado.");
        model.addAttribute("actionText", "Volver al inicio");
        model.addAttribute("actionUrl", "/videoclub");
        return "error/error";
    }
}