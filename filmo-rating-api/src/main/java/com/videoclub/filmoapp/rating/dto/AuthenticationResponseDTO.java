package com.videoclub.filmoapp.rating.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class AuthenticationResponseDTO {

    String accessToken;
    String tokenType;
    long expiresIn;

}