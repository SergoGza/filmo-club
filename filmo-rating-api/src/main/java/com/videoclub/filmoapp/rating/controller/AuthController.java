package com.videoclub.filmoapp.rating.controller;

import com.videoclub.filmoapp.rating.advice.InvalidGrantTypeException;
import com.videoclub.filmoapp.rating.advice.UnauthorizedException;
import com.videoclub.filmoapp.rating.dto.AuthenticationResponseDTO;
import com.videoclub.filmoapp.rating.service.AuthService;
import com.videoclub.filmoapp.rating.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/authenticate")
@Tag(name = "Authentication", description = "OAuth2 Client Credentials authentication")
public class AuthController {

  private final AuthService authService;
  private final JwtService jwtService;


  @PostMapping
  @Operation(
          summary = "Get access token",
          security = @SecurityRequirement(name = "basicAuth")
  )
  @ApiResponse(responseCode = "200", description = "Authentication successful",
          content = @Content(schema = @Schema(implementation = AuthenticationResponseDTO.class)))
  @ApiResponse(responseCode = "400", description = "Invalid grant type")
  @ApiResponse(responseCode = "401", description = "Invalid credentials")
  public ResponseEntity<?> authenticate(
          @Parameter(description = "Basic Auth header", required = true)
          @RequestHeader("Authorization") String authorization,

          @Parameter(description = "Grant type (use 'client_credentials')", example = "client_credentials")
          @RequestParam(value = "grant_type", required = false) String grantTypeParam,

          HttpServletRequest request) {

    String grantType = grantTypeParam;
    if (grantType == null || grantType.isEmpty()) {
      grantType = request.getParameter("grant_type");
    }

    log.debug("authenticate() called - Authorization present: {}, grant_type: {}",
            authorization != null, grantType);

    if (grantType == null || grantType.isEmpty()) {
      return ResponseEntity.badRequest().body("grant_type parameter is required");
    }

    try {
      AuthenticationResponseDTO response = authService.authenticate(grantType, authorization);
      return ResponseEntity.ok(response);
    } catch (InvalidGrantTypeException e) {
      log.error("Invalid grant type: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (UnauthorizedException e) {
      log.error("Unauthorized: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    } catch (Exception e) {
      log.error("Authentication error", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
    }
  }
}