package com.videoclub.filmoapp.film.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.videoclub.filmoapp.auth.domain.Role;
import com.videoclub.filmoapp.auth.domain.User;
import com.videoclub.filmoapp.auth.repository.RoleDAO;
import com.videoclub.filmoapp.auth.repository.UserDAO;
import com.videoclub.filmoapp.film.domain.Artist;
import com.videoclub.filmoapp.film.domain.ArtistType;
import com.videoclub.filmoapp.film.domain.Film;
import com.videoclub.filmoapp.film.domain.FilmImage;
import com.videoclub.filmoapp.film.dto.FilmDTO;
import com.videoclub.filmoapp.film.mvc.dto.FilmMvcDTO;
import com.videoclub.filmoapp.film.repository.ArtistDAO;
import com.videoclub.filmoapp.film.repository.FilmDAO;
import com.videoclub.filmoapp.film.service.FilmImageService;
import com.videoclub.filmoapp.film.service.FilmService;
import com.videoclub.filmoapp.rating.client.RatingClient;
import com.videoclub.filmoapp.rating.client.impl.RatingClientImpl;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest(
        properties = {
                "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MYSQL;DATABASE_TO_LOWER=TRUE",
                "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
                "spring.jpa.hibernate.ddl-auto=create-drop"
        })
@Transactional
class FilmServiceIntegrationTest {

    @Autowired
    private FilmService filmService;

    @Autowired
    private FilmDAO filmDAO;

    @Autowired
    private ArtistDAO artistDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private RoleDAO roleDAO;

    @MockBean
    private RatingClient ratingClient;

    @MockBean
    private FilmImageService filmImageService;

    private Artist director;
    private Artist actor1;
    private Artist actor2;
    private User user;
    private Film existingFilm;

    @BeforeEach
    void setUp() {
        // Create test data
        director = artistDAO.save(Artist.builder()
                .name("Christopher")
                .surname("Nolan")
                .artistType(ArtistType.DIRECTOR)
                .build());

        actor1 = artistDAO.save(Artist.builder()
                .name("Christian")
                .surname("Bale")
                .artistType(ArtistType.ACTOR)
                .build());

        actor2 = artistDAO.save(Artist.builder()
                .name("Heath")
                .surname("Ledger")
                .artistType(ArtistType.ACTOR)
                .build());

        // Create role and user
        Role userRole = roleDAO.save(Role.builder()
                .name("USER")
                .build());

        user = userDAO.save(User.builder()
                .username("testuser")
                .password("password")
                .email("test@test.com")
                .name("Test")
                .surname("User")
                .roles(Set.of(userRole))
                .build());

        // Set up security context
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user.getUsername(), null)
        );

        // Create existing film for testing
        existingFilm = filmDAO.save(Film.builder()
                .title("The Dark Knight")
                .releaseYear(2008)
                .director(director)
                .actors(Set.of(actor1, actor2))
                .user(user)
                .build());

        FilmImage dummyImage = new FilmImage();
        dummyImage.setResourceID(UUID.randomUUID());
        dummyImage.setFilename("poster.jpg");
        dummyImage.setContentType("image/jpeg");
        dummyImage.setSize(10);

        when(filmImageService.saveImage(any(MultipartFile.class))).thenReturn(dummyImage);
    }

    @Test
    @DisplayName("Should create a new film successfully")
    void createFilm_Success() {
        // Given
        FilmMvcDTO filmMvcDTO = FilmMvcDTO.builder()
                .title("Inception")
                .releaseYear(2010)
                .directorId(director.getId())
                .actorIds(List.of(actor1.getId()))
                .build();

        MockMultipartFile posterFile = new MockMultipartFile(
                "poster",
                "poster.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        // When
        FilmDTO result = filmService.createFilm(filmMvcDTO, posterFile);

        // Then
        assertNotNull(result);
        assertEquals("Inception", result.getTitle());
        assertEquals(2010, result.getReleaseYear());
        assertEquals("Christopher Nolan", result.getDirectorName());
        assertTrue(result.getActorNames().contains("Christian Bale"));

        // Verify film was saved in database
        List<Film> films = filmDAO.findAll();
        assertEquals(2, films.size()); // existingFilm + new film
    }

    @Test
    @DisplayName("Should get film by ID successfully")
    void getFilm_Success() {
        // When
        FilmDTO result = filmService.getFilm(existingFilm.getId());

        // Then
        assertNotNull(result);
        assertEquals("The Dark Knight", result.getTitle());
        assertEquals(2008, result.getReleaseYear());
        assertEquals("Christopher Nolan", result.getDirectorName());
        assertEquals(2, result.getActorNames().size());
    }

    @Test
    @DisplayName("Should throw exception when film not found")
    void getFilm_NotFound() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            filmService.getFilm(999L);
        });
    }

    @Test
    @DisplayName("Should get films with pagination")
    void getFilms_WithPagination() {
        // Given
        filmDAO.save(Film.builder()
                .title("Batman Begins")
                .releaseYear(2005)
                .director(director)
                .actors(Set.of(actor1))
                .user(user)
                .build());

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<FilmDTO> result = filmService.getFilms(null, pageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    @DisplayName("Should search films by title")
    void getFilms_SearchByTitle() {
        // Given
        filmDAO.save(Film.builder()
                .title("Batman Begins")
                .releaseYear(2005)
                .director(director)
                .actors(Set.of(actor1))
                .user(user)
                .build());

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<FilmDTO> result = filmService.getFilms("Batman", pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Batman Begins", result.getContent().get(0).getTitle());
    }

    @Test
    @DisplayName("Should edit existing film successfully")
    void editFilm_Success() {
        // Given
        FilmMvcDTO filmMvcDTO = FilmMvcDTO.builder()
                .id(existingFilm.getId())
                .title("The Dark Knight Rises")
                .releaseYear(2012)
                .directorId(director.getId())
                .actorIds(List.of(actor1.getId()))
                .build();

        // When
        FilmDTO result = filmService.editFilm(filmMvcDTO, null);

        // Then
        assertNotNull(result);
        assertEquals("The Dark Knight Rises", result.getTitle());
        assertEquals(2012, result.getReleaseYear());

        // Verify changes in database
        Film updatedFilm = filmDAO.findById(existingFilm.getId()).orElseThrow();
        assertEquals("The Dark Knight Rises", updatedFilm.getTitle());
        assertEquals(2012, updatedFilm.getReleaseYear());
    }

    @Test
    @DisplayName("Should get user rating for film")
    void getUserRatingForFilm_Success() {
        // Given
        Long filmId = existingFilm.getId();
        Long userId = user.getId();
        Integer expectedRating = 5;

        when(ratingClient.getUserRating(filmId, userId)).thenReturn(expectedRating);

        // When
        Integer result = filmService.getUserRatingForFilm(filmId, userId);

        // Then
        assertEquals(expectedRating, result);
    }

    @Test
    @DisplayName("Should get average rating for film")
    void getAverageRatingForFilm_Success() {
        // Given
        Long filmId = existingFilm.getId();
        RatingClientImpl.AverageRatingResponseDTO expectedResponse =
                new RatingClientImpl.AverageRatingResponseDTO(4.5, 10L);

        when(ratingClient.getAverageRating(filmId)).thenReturn(expectedResponse);

        // When
        RatingClientImpl.AverageRatingResponseDTO result = filmService.getAverageRatingForFilm(filmId);

        // Then
        assertNotNull(result);
        assertEquals(4.5, result.average());
        assertEquals(10L, result.ratings());
    }

    @Test
    @DisplayName("Should create rating for film")
    void createRatingForFilm_Success() {
        // Given
        Long filmId = existingFilm.getId();
        Long userId = user.getId();
        Integer score = 4;

        // When
        assertDoesNotThrow(() -> {
            filmService.createRatingForFilm(filmId, userId, score);
        });

        // Then
        Mockito.verify(ratingClient).createRating(filmId, userId, score);
    }

    @Test
    @DisplayName("Should throw exception when director not found")
    void createFilm_DirectorNotFound() {
        // Given
        FilmMvcDTO filmMvcDTO = FilmMvcDTO.builder()
                .title("Test Film")
                .releaseYear(2024)
                .directorId(999L) // Non-existent director
                .actorIds(List.of(actor1.getId()))
                .build();

        MockMultipartFile posterFile = new MockMultipartFile(
                "poster",
                "poster.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            filmService.createFilm(filmMvcDTO, posterFile);
        });
    }

    @Test
    @DisplayName("Should throw exception when actor not found")
    void createFilm_ActorNotFound() {
        // Given
        FilmMvcDTO filmMvcDTO = FilmMvcDTO.builder()
                .title("Test Film")
                .releaseYear(2024)
                .directorId(director.getId())
                .actorIds(List.of(999L)) // Non-existent actor
                .build();

        MockMultipartFile posterFile = new MockMultipartFile(
                "poster",
                "poster.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            filmService.createFilm(filmMvcDTO, posterFile);
        });
    }
}