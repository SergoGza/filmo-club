package com.videoclub.filmoapp.rating.controller;

import com.videoclub.filmoapp.rating.advice.InvalidGrantTypeException;
import com.videoclub.filmoapp.rating.advice.UnauthorizedException;
import com.videoclub.filmoapp.rating.dto.AuthenticationResponseDTO;
import com.videoclub.filmoapp.rating.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/authenticate")
public class AuthController {

  private final AuthService authService;

  @PostMapping
  public ResponseEntity<?> authenticate(
      @RequestHeader("Authorization") String authorization,
      @RequestParam("grant_type") String grantType) {

    log.debug("authenticate() called - Authorization: {}, grant_type: {}", authorization, grantType);


    try {
      AuthenticationResponseDTO response = authService.authenticate(grantType, authorization);
      return ResponseEntity.ok(response);
    } catch (InvalidGrantTypeException invalidGrantTypeException) {
      return ResponseEntity.badRequest().body(invalidGrantTypeException.getMessage());
    } catch (UnauthorizedException unauthorizedException) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(unauthorizedException.getMessage());
    }
  }
}
