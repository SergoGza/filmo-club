package com.videoclub.filmoapp.film.service.impl;

import com.videoclub.filmoapp.film.domain.Artist;
import com.videoclub.filmoapp.film.domain.Film;
import com.videoclub.filmoapp.film.dto.FilmDTO;
import com.videoclub.filmoapp.film.mvc.dto.FilmMvcDTO;
import com.videoclub.filmoapp.film.repository.ArtistDAO;
import com.videoclub.filmoapp.film.repository.FilmDAO;
import com.videoclub.filmoapp.film.service.FilmService;
import com.videoclub.filmoapp.rating.client.RatingClient;
import com.videoclub.filmoapp.rating.client.impl.RatingClientImpl;
import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {

  private final FilmDAO filmDAO;
  private final ArtistDAO artistDAO;
  private final ModelMapper modelMapper;
  private final RatingClient ratingClient;

  @Override
  public FilmDTO getFilm(Long filmId) {

    return filmDAO
        .findById(filmId)
        .map(film -> modelMapper.map(film, FilmDTO.class))
        .orElseThrow(
            () -> new IllegalArgumentException("Film with id:%s not found".formatted(filmId)));
  }

  @Override
  public Page<FilmDTO> getFilms(String title, Pageable pageable) {
    Page<Film> filmPage;

    if (title != null && !title.trim().isEmpty()) {
      filmPage = filmDAO.findByTitleLike("%" + title + "%", pageable);
    } else {
      filmPage = filmDAO.findAll(pageable);
    }

    return filmPage.map(film -> modelMapper.map(film, FilmDTO.class));
  }

  @Override
  @Transactional
  public FilmDTO createFilm(FilmMvcDTO filmMvcDTO) {

    Film film = createOrEdit(new Film(), filmMvcDTO);
    return modelMapper.map(film, FilmDTO.class);
  }

  @Override
  @Transactional
  public FilmDTO editFilm(FilmMvcDTO filmMvcDTO) {

    Film film =
        filmDAO
            .findById(filmMvcDTO.getId())
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Flight with id:%s not found".formatted(filmMvcDTO.getId())));

    film = createOrEdit(film, filmMvcDTO);
    return modelMapper.map(film, FilmDTO.class);
  }

  protected Film createOrEdit(Film film, FilmMvcDTO filmMvcDTO) {

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

    film.setTitle(filmMvcDTO.getTitle());
    film.setReleaseYear(filmMvcDTO.getReleaseYear());
    film.setDirector(director);
    film.setActors(actorOrActors);

    return filmDAO.save(film);
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
