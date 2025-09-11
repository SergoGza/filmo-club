package com.videoclub.filmoapp.film.mvc.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.videoclub.filmoapp.auth.domain.User;
import com.videoclub.filmoapp.auth.service.UserService;
import com.videoclub.filmoapp.film.domain.ArtistType;
import com.videoclub.filmoapp.film.dto.ArtistDTO;
import com.videoclub.filmoapp.film.dto.FilmDTO;
import com.videoclub.filmoapp.film.mvc.dto.FilmMvcDTO;
import com.videoclub.filmoapp.film.service.ArtistService;
import com.videoclub.filmoapp.film.service.FilmService;
import com.videoclub.filmoapp.rating.client.impl.RatingClientImpl;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FilmMvcController.class)
class FilmMvcControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FilmService filmService;

    @MockBean
    private ArtistService artistService;

    @MockBean
    private UserService userService;

    private FilmDTO sampleFilmDTO;

    @BeforeEach
    void setUp() {
        // Setup sample data
        sampleFilmDTO = FilmDTO.builder()
                .id(1L)
                .title("Test Film")
                .releaseYear(2024)
                .directorName("Test Director")
                .actorNames(List.of("Actor 1", "Actor 2"))
                .build();

        List<ArtistDTO> directors = Arrays.asList(
                ArtistDTO.builder()
                        .id(1L)
                        .name("Director")
                        .surname("One")
                        .artistType(ArtistType.DIRECTOR)
                        .build()
        );

        List<ArtistDTO> actors = Arrays.asList(
                ArtistDTO.builder()
                        .id(2L)
                        .name("Actor")
                        .surname("One")
                        .artistType(ArtistType.ACTOR)
                        .build(),
                ArtistDTO.builder()
                        .id(3L)
                        .name("Actor")
                        .surname("Two")
                        .artistType(ArtistType.ACTOR)
                        .build()
        );

        User mockUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@test.com")
                .build();

        // Setup common mocks
        when(artistService.getArtistsByType(String.valueOf(ArtistType.DIRECTOR))).thenReturn(directors);
        when(artistService.getArtistsByType(String.valueOf(ArtistType.ACTOR))).thenReturn(actors);
        when(userService.findByUsernameOrEmail(anyString())).thenReturn(mockUser);
    }

    @Test
    @DisplayName("Should get film detail page successfully")
    @WithMockUser
    void getFilmDetail_Success() throws Exception {
        // Given
        when(filmService.getFilm(1L)).thenReturn(sampleFilmDTO);
        when(filmService.getAverageRatingForFilm(1L))
                .thenReturn(new RatingClientImpl.AverageRatingResponseDTO(4.5, 10L));
        when(filmService.getUserRatingForFilm(1L, 1L)).thenReturn(5);

        // When & Then
        mockMvc.perform(get("/videoclub/film/films/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("videoclub/film/film-detail"))
                .andExpect(model().attributeExists("film", "userRating", "averageRating", "totalRatings"))
                .andExpect(model().attribute("film", sampleFilmDTO))
                .andExpect(model().attribute("userRating", 5))
                .andExpect(model().attribute("averageRating", 4.5));
    }

    @Test
    @DisplayName("Should submit rating successfully")
    @WithMockUser
    void submitRating_Success() throws Exception {
        // When & Then
        mockMvc.perform(post("/videoclub/film/rating/1")
                        .param("score", "5")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/videoclub/film/films/1"))
                .andExpect(flash().attribute("successMessage", "Rating submitted successfully!"));

        verify(filmService).createRatingForFilm(1L, 1L, 5);
    }

    @Test
    @DisplayName("Should handle rating submission error")
    @WithMockUser
    void submitRating_Error() throws Exception {
        // Given
        doThrow(new RuntimeException("Rating already exists"))
                .when(filmService).createRatingForFilm(1L, 1L, 5);

        // When & Then
        mockMvc.perform(post("/videoclub/film/rating/1")
                        .param("score", "5")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/videoclub/film/films/1"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    @DisplayName("Should get search films page")
    @WithMockUser
    void searchFilms_Page() throws Exception {
        mockMvc.perform(get("/videoclub/film/search"))
                .andExpect(status().isOk())
                .andExpect(view().name("videoclub/film/search-films"));
    }

    @Test
    @DisplayName("Should get films list with pagination")
    @WithMockUser
    void getFilms_WithPagination() throws Exception {
        // Given
        Page<FilmDTO> filmPage = new PageImpl<>(
                List.of(sampleFilmDTO),
                PageRequest.of(0, 3),
                1
        );
        when(filmService.getFilms(isNull(), any(Pageable.class))).thenReturn(filmPage);

        // When & Then
        mockMvc.perform(get("/videoclub/film/films")
                        .param("page", "0")
                        .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(view().name("videoclub/film/films"))
                .andExpect(model().attributeExists("films"))
                .andExpect(model().attribute("films", filmPage));
    }

    @Test
    @DisplayName("Should search films by title")
    @WithMockUser
    void getFilms_SearchByTitle() throws Exception {
        // Given
        Page<FilmDTO> filmPage = new PageImpl<>(
                List.of(sampleFilmDTO),
                PageRequest.of(0, 3),
                1
        );
        when(filmService.getFilms(eq("Test"), any(Pageable.class))).thenReturn(filmPage);

        // When & Then
        mockMvc.perform(get("/videoclub/film/films")
                        .param("title", "Test")
                        .param("page", "0")
                        .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(view().name("videoclub/film/films"))
                .andExpect(model().attribute("title", "Test"))
                .andExpect(model().attribute("films", filmPage));
    }

    @Test
    @DisplayName("Should get create film page - Admin only")
    @WithMockUser(roles = "ADMIN")
    void createFilm_GetPage() throws Exception {
        mockMvc.perform(get("/videoclub/film/films-edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("videoclub/film/films-edit"))
                .andExpect(model().attributeExists("film", "directors", "actors"));
    }

    @Test
    @DisplayName("Should get edit film page - Admin only")
    @WithMockUser(roles = "ADMIN")
    void editFilm_GetPage() throws Exception {
        // Given
        FilmMvcDTO filmMvcDTO = FilmMvcDTO.builder()
                .id(1L)
                .title("Test Film")
                .releaseYear(2024)
                .directorId(1L)
                .actorIds(List.of(2L, 3L))
                .build();

        when(filmService.getFilmForEdit(1L)).thenReturn(filmMvcDTO);

        // When & Then
        mockMvc.perform(get("/videoclub/film/films-edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("videoclub/film/films-edit"))
                .andExpect(model().attribute("film", filmMvcDTO))
                .andExpect(model().attributeExists("directors", "actors"));
    }

    @Test
    @DisplayName("Should create film successfully - Admin only")
    @WithMockUser(roles = "ADMIN")
    void createFilm_Post_Success() throws Exception {
        // Given
        MockMultipartFile poster = new MockMultipartFile(
                "poster",
                "poster.jpg",
                "image/jpeg",
                "test image".getBytes()
        );

        when(filmService.createFilm(any(FilmMvcDTO.class), any())).thenReturn(sampleFilmDTO);

        // When & Then
        mockMvc.perform(multipart("/videoclub/film/films-edit")
                        .file(poster)
                        .param("title", "New Film")
                        .param("releaseYear", "2024")
                        .param("directorId", "1")
                        .param("actorIds", "2", "3")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/videoclub/film/films"));
    }

    @Test
    @DisplayName("Should validate required poster for new films")
    @WithMockUser(roles = "ADMIN")
    void createFilm_NoPoster_ValidationError() throws Exception {
        // When & Then - No poster file
        mockMvc.perform(multipart("/videoclub/film/films-edit")
                        .param("title", "New Film")
                        .param("releaseYear", "2024")
                        .param("directorId", "1")
                        .param("actorIds", "2", "3")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("org.springframework.validation.BindingResult.film"));
    }

    @Test
    @DisplayName("Should edit film successfully - Admin only")
    @WithMockUser(roles = "ADMIN")
    void editFilm_Post_Success() throws Exception {
        // Given
        when(filmService.editFilm(any(FilmMvcDTO.class), isNull())).thenReturn(sampleFilmDTO);

        // When & Then
        mockMvc.perform(multipart("/videoclub/film/films-edit/1")
                        .param("id", "1")
                        .param("title", "Updated Film")
                        .param("releaseYear", "2025")
                        .param("directorId", "1")
                        .param("actorIds", "2")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/videoclub/film/films"));
    }


    @Test
    @DisplayName("Should handle service exception during film creation")
    @WithMockUser(roles = "ADMIN")
    void createFilm_ServiceException() throws Exception {
        // Given
        MockMultipartFile poster = new MockMultipartFile(
                "poster",
                "poster.jpg",
                "image/jpeg",
                "test image".getBytes()
        );

        when(filmService.createFilm(any(FilmMvcDTO.class), any()))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(multipart("/videoclub/film/films-edit")
                        .file(poster)
                        .param("title", "New Film")
                        .param("releaseYear", "2024")
                        .param("directorId", "1")
                        .param("actorIds", "2", "3")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("videoclub/film/films-edit"))
                .andExpect(model().attributeExists("org.springframework.validation.BindingResult.film"));
    }

    @Test
    @DisplayName("Should require authentication for film details")
    void getFilmDetail_RequiresAuth() throws Exception {
        mockMvc.perform(get("/videoclub/film/films/1"))
                .andExpect(status().isUnauthorized());
    }

}