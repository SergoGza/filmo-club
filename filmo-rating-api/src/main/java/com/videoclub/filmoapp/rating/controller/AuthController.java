package com.videoclub.filmoapp.rating.controller;

import com.videoclub.filmoapp.rating.advice.InvalidGrantTypeException;
import com.videoclub.filmoapp.rating.advice.UnauthorizedException;
import com.videoclub.filmoapp.rating.dto.AuthenticationResponseDTO;
import com.videoclub.filmoapp.rating.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/authenticate")
public class AuthController {

  private final AuthService authService;

  @PostMapping("/authenticate")
  public ResponseEntity<?> authenticate(
      @RequestHeader("Authorization") String authorization,
      @RequestParam("grant_type") String grantType) {

    try {
      AuthenticationResponseDTO response = authService.authenticate(authorization, grantType);
      return ResponseEntity.ok(response);
    } catch (InvalidGrantTypeException invalidGrantTypeException) {
      return ResponseEntity.badRequest().body(invalidGrantTypeException.getMessage());
    } catch (UnauthorizedException unauthorizedException) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(unauthorizedException.getMessage());
    }
  }
}
