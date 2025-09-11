package com.videoclub.filmoapp.security;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.videoclub.filmoapp.auth.config.service.CustomUserDetailsService;
import com.videoclub.filmoapp.auth.domain.Role;
import com.videoclub.filmoapp.auth.domain.User;
import com.videoclub.filmoapp.auth.service.UserService;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private User mockUser;
    private User mockAdmin;

    @BeforeEach
    void setUp() {
        // Setup mock users
        Role userRole = Role.builder().id(1L).name("USER").build();
        Role adminRole = Role.builder().id(2L).name("ADMIN").build();

        mockUser = User.builder()
                .id(1L)
                .username("user")
                .password("password")
                .email("user@test.com")
                .roles(Set.of(userRole))
                .build();

        mockAdmin = User.builder()
                .id(2L)
                .username("admin")
                .password("password")
                .email("admin@test.com")
                .roles(Set.of(adminRole))
                .build();

        when(userService.findByUsernameOrEmail("user")).thenReturn(mockUser);
        when(userService.findByUsernameOrEmail("admin")).thenReturn(mockAdmin);
    }

    // ========== PUBLIC ENDPOINTS TESTS ==========

    @Test
    @DisplayName("Login page should be accessible to everyone")
    @WithAnonymousUser
    void loginPage_PublicAccess() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Register page should be accessible to everyone")
    @WithAnonymousUser
    void registerPage_PublicAccess() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Homepage should be accessible to everyone")
    @WithAnonymousUser
    void homepage_PublicAccess() throws Exception {
        mockMvc.perform(get("/videoclub"))
                .andExpect(status().isOk());
    }

    // ========== AUTHENTICATED USER ENDPOINTS TESTS ==========

    @Test
    @DisplayName("Film list should require authentication")
    @WithAnonymousUser
    void filmList_RequiresAuth() throws Exception {
        mockMvc.perform(get("/videoclub/film/films"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("Film list should be accessible to authenticated users")
    @WithMockUser(username = "user", roles = "USER")
    void filmList_AuthenticatedAccess() throws Exception {
        mockMvc.perform(get("/videoclub/film/films"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Film detail should require authentication")
    @WithAnonymousUser
    void filmDetail_RequiresAuth() throws Exception {
        mockMvc.perform(get("/videoclub/film/films/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("Film detail should be accessible to authenticated users")
    @WithMockUser(username = "user", roles = "USER")
    void filmDetail_AuthenticatedAccess() throws Exception {
        mockMvc.perform(get("/videoclub/film/films/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Film search should require authentication")
    @WithAnonymousUser
    void filmSearch_RequiresAuth() throws Exception {
        mockMvc.perform(get("/videoclub/film/search"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("Film search should be accessible to authenticated users")
    @WithMockUser(username = "user", roles = "USER")
    void filmSearch_AuthenticatedAccess() throws Exception {
        mockMvc.perform(get("/videoclub/film/search"))
                .andExpect(status().isOk());
    }

    // ========== ADMIN-ONLY ENDPOINTS TESTS ==========

    @Test
    @DisplayName("Film creation should require admin role")
    @WithMockUser(username = "user", roles = "USER")
    void filmCreate_RequiresAdmin_UserDenied() throws Exception {
        mockMvc.perform(get("/videoclub/film/films-edit/"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Film creation should be accessible to admin")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void filmCreate_AdminAccess() throws Exception {
        mockMvc.perform(get("/videoclub/film/films-edit/"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Film edit should require admin role")
    @WithMockUser(username = "user", roles = "USER")
    void filmEdit_RequiresAdmin_UserDenied() throws Exception {
        mockMvc.perform(get("/videoclub/film/films-edit/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Film edit should be accessible to admin")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void filmEdit_AdminAccess() throws Exception {
        mockMvc.perform(get("/videoclub/film/films-edit/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Artist creation should require authentication")
    @WithAnonymousUser
    void artistCreate_RequiresAuth() throws Exception {
        mockMvc.perform(get("/videoclub/artist/create"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("Artist creation should be accessible to authenticated users")
    @WithMockUser(username = "user", roles = "USER")
    void artistCreate_AuthenticatedAccess() throws Exception {
        mockMvc.perform(get("/videoclub/artist/create"))
                .andExpect(status().isOk());
    }

    // ========== RATING ENDPOINTS TESTS ==========

    @Test
    @DisplayName("Rating submission should require authentication")
    @WithAnonymousUser
    void submitRating_RequiresAuth() throws Exception {
        mockMvc.perform(post("/videoclub/film/rating/1")
                        .param("score", "5")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("Rating submission should work for authenticated users")
    @WithMockUser(username = "user", roles = "USER")
    void submitRating_AuthenticatedAccess() throws Exception {
        mockMvc.perform(post("/videoclub/film/rating/1")
                        .param("score", "5")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    // ========== RESOURCE ENDPOINTS TESTS ==========

    @Test
    @DisplayName("Film resources should require authentication")
    @WithAnonymousUser
    void filmResources_RequiresAuth() throws Exception {
        mockMvc.perform(get("/videoclub/resources/test-resource-id"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("Film downloads should require authentication")
    @WithAnonymousUser
    void filmDownloads_RequiresAuth() throws Exception {
        mockMvc.perform(get("/videoclub/downloads/test-resource-id"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }


    // ========== CSRF PROTECTION TESTS ==========

    @Test
    @DisplayName("POST requests should require CSRF token")
    @WithMockUser(username = "user", roles = "USER")
    void postRequest_RequiresCSRF() throws Exception {
        // Without CSRF token
        mockMvc.perform(post("/videoclub/film/rating/1")
                        .param("score", "5"))
                .andExpect(status().isForbidden());

        // With CSRF token
        mockMvc.perform(post("/videoclub/film/rating/1")
                        .param("score", "5")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Register POST should work with CSRF")
    @WithAnonymousUser
    void register_PostWithCSRF() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", "newuser")
                        .param("password", "password")
                        .param("confirmPassword", "password")
                        .param("email", "new@test.com")
                        .param("name", "New")
                        .param("surname", "User")
                        .param("birthDate", "1990-01-01")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    // ========== ROLE HIERARCHY TESTS ==========

    @Test
    @DisplayName("Admin should have access to user endpoints")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void admin_HasUserAccess() throws Exception {
        // Admin can access regular authenticated endpoints
        mockMvc.perform(get("/videoclub/film/films"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/videoclub/film/search"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/videoclub/artist/create"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("User should not have access to admin endpoints")
    @WithMockUser(username = "user", roles = "USER")
    void user_NoAdminAccess() throws Exception {
        // User cannot access admin endpoints
        mockMvc.perform(get("/videoclub/film/films-edit/"))
                .andExpect(status().isForbidden());
    }
}