package com.videoclub.filmoapp.auth.service.impl;

import com.videoclub.filmoapp.auth.domain.Role;
import com.videoclub.filmoapp.auth.domain.User;
import com.videoclub.filmoapp.auth.dto.UserDTO;
import com.videoclub.filmoapp.auth.dto.UserMvcDTO;
import com.videoclub.filmoapp.auth.repository.RoleDAO;
import com.videoclub.filmoapp.auth.repository.UserDAO;
import com.videoclub.filmoapp.auth.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MYSQL;DATABASE_TO_LOWER=TRUE",
      "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
      "spring.jpa.hibernate.ddl-auto=create-drop"
    })
@Transactional
class UserServiceTest {

  @Autowired private UserService userService;

  @Autowired private UserDAO userDAO;

  @Autowired private RoleDAO roleDAO;

  @Autowired private PasswordEncoder passwordEncoder;

  @Autowired private EntityManager entityManager;

  private Role userRole;
  private Role adminRole;

  @BeforeEach
  void setUp() {
    // Create roles
    userRole = roleDAO.save(Role.builder().name("USER").build());

    adminRole = roleDAO.save(Role.builder().name("ADMIN").build());
  }

  @Test
  @DisplayName("Should register new user successfully")
  void registerUser_Success() {
    // Given
    UserMvcDTO userMvcDTO =
        UserMvcDTO.builder()
            .username("newuser")
            .password("password123")
            .confirmPassword("password123")
            .email("newuser@test.com")
            .name("New")
            .surname("User")
            .birthDate(LocalDate.of(1990, 1, 1))
            .build();

    // When
    assertDoesNotThrow(() -> userService.registerUser(userMvcDTO));

    // Then
    Optional<User> savedUser = userDAO.findByUsername("newuser");
    assertTrue(savedUser.isPresent());
    assertEquals("newuser@test.com", savedUser.get().getEmail());
    assertEquals("New", savedUser.get().getName());
    assertEquals("User", savedUser.get().getSurname());
    assertTrue(passwordEncoder.matches("password123", savedUser.get().getPassword()));
    assertTrue(savedUser.get().getRoles().contains(userRole));
  }

  @Test
  @DisplayName("Should throw exception when username already exists")
  void registerUser_UsernameExists() {
    // Given
    User existingUser =
        userDAO.save(
            User.builder()
                .username("existinguser")
                .password("password")
                .email("existing@test.com")
                .name("Existing")
                .surname("User")
                .roles(Set.of(userRole))
                .build());

    UserMvcDTO userMvcDTO =
        UserMvcDTO.builder()
            .username("existinguser") // Same username
            .password("password123")
            .confirmPassword("password123")
            .email("newemail@test.com")
            .name("New")
            .surname("User")
            .birthDate(LocalDate.of(1990, 1, 1))
            .build();

    // When & Then
    assertThrows(
        IllegalStateException.class,
        () -> {
          userService.registerUser(userMvcDTO);
        },
        "Username is already in use");
  }

  @Test
  @DisplayName("Should throw exception when email already exists")
  void registerUser_EmailExists() {
    // Given
    User existingUser =
        userDAO.save(
            User.builder()
                .username("existinguser")
                .password("password")
                .email("existing@test.com")
                .name("Existing")
                .surname("User")
                .roles(Set.of(userRole))
                .build());

    UserMvcDTO userMvcDTO =
        UserMvcDTO.builder()
            .username("newusername")
            .password("password123")
            .confirmPassword("password123")
            .email("existing@test.com") // Same email
            .name("New")
            .surname("User")
            .birthDate(LocalDate.of(1990, 1, 1))
            .build();

    // When & Then
    assertThrows(
        IllegalStateException.class,
        () -> {
          userService.registerUser(userMvcDTO);
        },
        "Email is already in use");
  }

  @Test
  @DisplayName("Should throw exception when passwords don't match")
  void registerUser_PasswordMismatch() {
    // Given
    UserMvcDTO userMvcDTO =
        UserMvcDTO.builder()
            .username("newuser")
            .password("password123")
            .confirmPassword("differentpassword") // Different password
            .email("newuser@test.com")
            .name("New")
            .surname("User")
            .birthDate(LocalDate.of(1990, 1, 1))
            .build();

    // When & Then
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          userService.registerUser(userMvcDTO);
        },
        "Passwords do not match");
  }

  @Test
  @DisplayName("Should find user by username")
  void findByUsername_Success() {
    // Given
    User user =
        userDAO.save(
            User.builder()
                .username("testuser")
                .password("password")
                .email("test@test.com")
                .name("Test")
                .surname("User")
                .roles(Set.of(userRole))
                .build());

    // When
    Optional<UserDTO> result = userService.findByUsername("testuser");

    // Then
    assertTrue(result.isPresent());
    assertEquals("testuser", result.get().getUsername());
    assertEquals("test@test.com", result.get().getEmail());
    assertEquals("Test", result.get().getName());
  }

  @Test
  @DisplayName("Should throw exception when username not found")
  void findByUsername_NotFound() {
    // When & Then
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          userService.findByUsername("nonexistent");
        });
  }

  @Test
  @DisplayName("Should find user by email")
  void findByEmail_Success() {
    // Given
    User user =
        userDAO.save(
            User.builder()
                .username("testuser")
                .password("password")
                .email("test@test.com")
                .name("Test")
                .surname("User")
                .roles(Set.of(userRole))
                .build());

    // When
    Optional<UserDTO> result = userService.findByEmail("test@test.com");

    // Then
    assertTrue(result.isPresent());
    assertEquals("testuser", result.get().getUsername());
    assertEquals("test@test.com", result.get().getEmail());
  }

  @Test
  @DisplayName("Should throw exception when email not found")
  void findByEmail_NotFound() {
    // When & Then
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          userService.findByEmail("nonexistent@test.com");
        });
  }

  @Test
  @DisplayName("Should find user by username or email - using username")
  void findByUsernameOrEmail_UsingUsername() {
    // Given
    User user =
        userDAO.save(
            User.builder()
                .username("testuser")
                .password("password")
                .email("test@test.com")
                .name("Test")
                .surname("User")
                .roles(Set.of(userRole))
                .build());

    // When
    User result = userService.findByUsernameOrEmail("testuser");

    // Then
    assertNotNull(result);
    assertEquals("testuser", result.getUsername());
    assertEquals("test@test.com", result.getEmail());
  }

  @Test
  @DisplayName("Should find user by username or email - using email")
  void findByUsernameOrEmail_UsingEmail() {
    // Given
    User user =
        userDAO.save(
            User.builder()
                .username("testuser")
                .password("password")
                .email("test@test.com")
                .name("Test")
                .surname("User")
                .roles(Set.of(userRole))
                .build());

    // When
    User result = userService.findByUsernameOrEmail("test@test.com");

    // Then
    assertNotNull(result);
    assertEquals("testuser", result.getUsername());
    assertEquals("test@test.com", result.getEmail());
  }

  @Test
  @DisplayName("Should throw exception when neither username nor email found")
  void findByUsernameOrEmail_NotFound() {
    // When & Then
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          userService.findByUsernameOrEmail("nonexistent");
        });
  }

    @Test
    @DisplayName("Should update last login timestamp")
    void performLastLoginAt_Success() {
        // Given
        User user =
                userDAO.save(
                        User.builder()
                                .username("testuser")
                                .password("password")
                                .email("test@test.com")
                                .name("Test")
                                .surname("User")
                                .build());

        LocalDateTime start = LocalDateTime.now();

        // When
        userService.performLastLoginAt(user);

        entityManager.flush();
        entityManager.clear();

        LocalDateTime end = LocalDateTime.now();

        // Then
        User updatedUser = userDAO.findById(user.getId()).orElseThrow();

        assertNotNull(updatedUser.getLastLoginAt());
        assertTrue(
                (updatedUser.getLastLoginAt().isAfter(start) || updatedUser.getLastLoginAt().isEqual(start))
                        && (updatedUser.getLastLoginAt().isBefore(end) || updatedUser.getLastLoginAt().isEqual(end)),
                () -> "Expected lastLoginAt between " + start + " and " + end +
                        " but was " + updatedUser.getLastLoginAt());
    }

  @Test
  @DisplayName("Should create user with admin role")
  void registerUser_WithAdminRole() {
    // Given
    User adminUser =
        userDAO.save(
            User.builder()
                .username("admin")
                .password(passwordEncoder.encode("adminpass"))
                .email("admin@test.com")
                .name("Admin")
                .surname("User")
                .roles(Set.of(adminRole))
                .build());

    // When
    User result = userService.findByUsernameOrEmail("admin");

    // Then
    assertNotNull(result);
    assertTrue(result.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN")));
  }

  @Test
  @DisplayName("Should handle user with multiple roles")
  void findUser_WithMultipleRoles() {
    // Given
    User superUser =
        userDAO.save(
            User.builder()
                .username("superuser")
                .password(passwordEncoder.encode("superpass"))
                .email("super@test.com")
                .name("Super")
                .surname("User")
                .roles(Set.of(userRole, adminRole))
                .build());

    // When
    User result = userService.findByUsernameOrEmail("superuser");

    // Then
    assertNotNull(result);
    assertEquals(2, result.getRoles().size());
    assertTrue(result.getRoles().stream().anyMatch(role -> role.getName().equals("USER")));
    assertTrue(result.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN")));
  }
}
