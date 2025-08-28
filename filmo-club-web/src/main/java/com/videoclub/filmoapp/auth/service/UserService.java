package com.videoclub.filmoapp.auth.service;

import com.videoclub.filmoapp.auth.domain.User;
import com.videoclub.filmoapp.auth.dto.UserDTO;
import com.videoclub.filmoapp.auth.dto.UserMvcDTO;

import java.util.Optional;

public interface UserService {

  void registerUser(UserMvcDTO userMvcDTO);

  Optional<UserDTO> findByUsername(String username);

  Optional<UserDTO> findByEmail(String email);

  User findByUsernameOrEmail(String usernameOrEmail);

  void performLastLoginAt(User user);
}
