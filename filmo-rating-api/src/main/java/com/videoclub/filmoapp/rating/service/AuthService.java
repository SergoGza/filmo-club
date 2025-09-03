package com.videoclub.filmoapp.rating.service;

import com.videoclub.filmoapp.rating.dto.AuthenticationResponseDTO;

public interface AuthService {

    public AuthenticationResponseDTO authenticate(String grantType, String authHeader);

}
