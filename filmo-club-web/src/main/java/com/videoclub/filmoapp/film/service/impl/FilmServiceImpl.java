package com.videoclub.filmoapp.film.service.impl;

import com.videoclub.filmoapp.auth.domain.User;
import com.videoclub.filmoapp.auth.service.UserService;
import com.videoclub.filmoapp.core.dto.ResourceDTO;
import com.videoclub.filmoapp.film.domain.Artist;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmServiceImpl implements FilmService {

  private final FilmDAO filmDAO;
  private final ArtistDAO artistDAO;
  private final RatingClient ratingClient;
  private final FilmImageService filmImageService;
  private final UserService userService;

  @Override
  @Transactional(readOnly = true)
  public FilmDTO getFilm(Long filmId) {
    return filmDAO
        .findById(filmId)
        .map(this::buildFilmDTO)
        .orElseThrow(
            () -> new IllegalArgumentException("Film with id:%s not found".formatted(filmId)));
  }

  @Transactional(readOnly = true)
  @Override
  public FilmMvcDTO getFilmForEdit(Long filmId) {
    Film film =
        filmDAO
            .findById(filmId)
            .orElseThrow(
                () -> new IllegalArgumentException("Film with id:%s not found".formatted(filmId)));

    return FilmMvcDTO.builder()
        .id(film.getId())
        .title(film.getTitle())
        .releaseYear(film.getReleaseYear())
        .directorId(film.getDirector() != null ? film.getDirector().getId() : null)
        .actorIds(film.getActors().stream().map(Artist::getId).toList())
        .build();
  }

  @Transactional(readOnly = true)
  @Override
  public Page<FilmDTO> getFilms(String title, Pageable pageable) {
    Page<Film> filmPage;

    if (title != null && !title.trim().isEmpty()) {
      filmPage = filmDAO.findByTitleLike("%" + title + "%", pageable);
    } else {
      filmPage = filmDAO.findAll(pageable);
    }

    return filmPage.map(this::buildFilmDTO);
  }

  @Override
  @Transactional
  public FilmDTO createFilm(FilmMvcDTO filmMvcDTO, MultipartFile posterFile) {
    log.info("CREANDO PELÍCULA: {}", filmMvcDTO.getTitle());
    Film film = createOrEdit(new Film(), filmMvcDTO, posterFile);
    log.info("PELÍCULA GUARDADA CON ID: {}", film.getId());
    return buildFilmDTO(film);
  }

  @Override
  @Transactional
  public FilmDTO editFilm(FilmMvcDTO filmMvcDTO, MultipartFile posterFile) {
    Film film =
        filmDAO
            .findById(filmMvcDTO.getId())
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Film with id:%s not found".formatted(filmMvcDTO.getId())));

    film = createOrEdit(film, filmMvcDTO, posterFile);
    return buildFilmDTO(film);
  }

  protected Film createOrEdit(Film film, FilmMvcDTO filmMvcDTO, MultipartFile posterFile) {

    Artist director =
        artistDAO
            .findById(filmMvcDTO.getDirectorId())
            .orElseThrow(() -> new IllegalArgumentException("Director not found"));

    Set<Artist> actorOrActors =
        new HashSet<>(
            Optional.ofNullable(filmMvcDTO.getActorIds()).orElse(Collections.emptyList()).stream()
                .map(
                    id ->
                        artistDAO
                            .findById(id)
                            .orElseThrow(
                                () ->
                                    new IllegalArgumentException(
                                        "Actor with id:%s not found ".formatted(id))))
                .toList());

    FilmImage filmImage = film.getImage();
    if (posterFile != null && !posterFile.isEmpty()) {
      // Si hay imagen anterior, eliminarla
      if (filmImage != null && filmImage.getResourceID() != null) {
        filmImageService.deleteImage(filmImage.getResourceID());
      }

      filmImage = filmImageService.saveImage(posterFile);
      filmImage.setFilm(film);
    }

    if (film.getUser() == null) {
      String username = SecurityContextHolder.getContext().getAuthentication().getName();
      User user = userService.findByUsernameOrEmail(username);
      film.setUser(user);
    }



    film.setTitle(filmMvcDTO.getTitle());
    film.setReleaseYear(filmMvcDTO.getReleaseYear());
    film.setDirector(director);
    film.setActors(actorOrActors);
    film.setImage(filmImage);

    return filmDAO.save(film);
  }

  private FilmDTO buildFilmDTO(Film film) {
    ResourceDTO imageDTO = null;
    if (film.getImage() != null) {
      imageDTO =
          ResourceDTO.builder()
              .resourceId(film.getImage().getResourceID())
              .filename(film.getImage().getFilename())
              .contentType(film.getImage().getContentType())
              .size(film.getImage().getSize())
              .build();
    }

    return FilmDTO.builder()
        .id(film.getId())
        .title(film.getTitle())
        .releaseYear(film.getReleaseYear())
        .directorName(
            film.getDirector() != null
                ? film.getDirector().getName() + " " + film.getDirector().getSurname()
                : null)
        .actorNames(
            film.getActors().stream()
                .map(actor -> actor.getName() + " " + actor.getSurname())
                .toList())
        .image(imageDTO)
        .build();
  }

  @Override
  public Integer getUserRatingForFilm(Long filmId, Long userId) {
    return ratingClient.getUserRating(filmId, userId);
  }

  @Override
  public RatingClientImpl.AverageRatingResponseDTO getAverageRatingForFilm(Long filmId) {

    return ratingClient.getAverageRating(filmId);
  }

  @Override
  public void createRatingForFilm(Long filmId, Long userId, Integer score) {

    ratingClient.createRating(filmId, userId, score);
  }
}
