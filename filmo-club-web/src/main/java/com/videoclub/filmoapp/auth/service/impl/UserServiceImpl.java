package com.videoclub.filmoapp.auth.service.impl;

import com.videoclub.filmoapp.auth.domain.User;
import com.videoclub.filmoapp.auth.dto.UserDTO;
import com.videoclub.filmoapp.auth.dto.UserMvcDTO;
import com.videoclub.filmoapp.auth.repository.RoleDAO;
import com.videoclub.filmoapp.auth.repository.UserDAO;
import com.videoclub.filmoapp.auth.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserDAO userDAO;
  private final RoleDAO roleDAO;
  private final PasswordEncoder passwordEncoder;
  private final ModelMapper modelMapper;

  @Transactional
  @Override
  public void registerUser(UserMvcDTO userMvcDTO) {

    if (userDAO.findByUsername(userMvcDTO.getUsername()).isPresent()) {
      throw new IllegalStateException("Username is already in use");
    }

    if (userDAO.findByEmail(userMvcDTO.getEmail()).isPresent()) {

      throw new IllegalStateException("Email is already in use");
    }

    if (!userMvcDTO.getPassword().equals(userMvcDTO.getConfirmPassword())) {
      throw new IllegalArgumentException("Passwords do not match");
    }

    User user =
        User.builder()
            .username(userMvcDTO.getUsername())
            .email(userMvcDTO.getEmail())
            .password(passwordEncoder.encode(userMvcDTO.getPassword()))
            .birthDate(userMvcDTO.getBirthDate())
            .roles(
                Set.of(
                    roleDAO
                        .findByName("USER")
                        .orElseThrow(() -> new IllegalStateException("Role not found"))))
            .name(userMvcDTO.getName())
            .surname(userMvcDTO.getSurname())
            .build();

    userDAO.save(user);
  }

  @Override
  public Optional<UserDTO> findByEmail(String email) {

    Optional<User> maybeUser = userDAO.findByEmail(email);

    return Optional.ofNullable(
        maybeUser
            .map(user -> modelMapper.map(user, UserDTO.class))
            .orElseThrow(
                () ->
                    new IllegalArgumentException("User with email:%s not found".formatted(email))));
  }

  @Override
  public Optional<UserDTO> findByUsername(String username) {

    Optional<User> maybeUser = userDAO.findByUsername(username);
    return Optional.ofNullable(
        maybeUser
            .map(user -> modelMapper.map(user, UserDTO.class))
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "User with username:%s not found".formatted(username))));
  }

  @Override
  public User findByUsernameOrEmail(String usernameOrEmail) {

    return userDAO
        .findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "User with email or username :%s not found".formatted(usernameOrEmail)));
  }

  @Override
  @Transactional
  public void performLastLoginAt(User user) {

    user.setLastLoginAt(LocalDateTime.now());
    userDAO.save(user);
  }
}
