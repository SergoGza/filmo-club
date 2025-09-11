package com.videoclub.filmoapp.web.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {

        // Get error information
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object errorMessage = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        String requestUri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

        Integer statusCode = null;
        if (status != null) {
            statusCode = Integer.valueOf(status.toString());
        }

        // Log error for debugging
        log.error("Error {} at {}: {}", statusCode, requestUri, errorMessage);
        if (exception != null) {
            log.error("Exception: ", (Throwable) exception);
        }

        // Add information to model
        model.addAttribute("statusCode", statusCode);
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("requestUri", requestUri);
        model.addAttribute("timestamp", java.time.LocalDateTime.now());

        // Determine which page to show
        if (statusCode != null) {
            switch (statusCode) {
                case 401:
                    model.addAttribute("errorTitle", "Unauthorized");
                    model.addAttribute("errorDescription", "You need to log in to access this page.");
                    model.addAttribute("actionText", "Go to Login");
                    model.addAttribute("actionUrl", "/login");
                    return "error/401";

                case 403:
                    model.addAttribute("errorTitle", "Access Denied");
                    model.addAttribute("errorDescription", "You don't have permission to access this resource.");
                    model.addAttribute("actionText", "Back to Home");
                    model.addAttribute("actionUrl", "/videoclub");
                    return "error/403";

                case 404:
                    model.addAttribute("errorTitle", "Page Not Found");
                    model.addAttribute("errorDescription", "The page you're looking for doesn't exist or has been moved.");
                    model.addAttribute("actionText", "Back to Home");
                    model.addAttribute("actionUrl", "/videoclub");
                    return "error/404";

                case 500:
                    model.addAttribute("errorTitle", "Internal Server Error");
                    model.addAttribute("errorDescription", "An unexpected error occurred. Please try again later.");
                    model.addAttribute("actionText", "Back to Home");
                    model.addAttribute("actionUrl", "/videoclub");
                    return "error/500";

            }
        }

        // Generic error
        model.addAttribute("errorTitle", "Error");
        model.addAttribute("errorDescription", "An unexpected error occurred.");
        model.addAttribute("actionText", "Back to Home");
        model.addAttribute("actionUrl", "/videoclub");
        return "error/error";
    }
}