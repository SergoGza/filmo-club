package com.videoclub.filmoapp.film.service;

import com.videoclub.filmoapp.film.dto.FilmDTO;
import com.videoclub.filmoapp.film.mvc.dto.FilmMvcDTO;
import com.videoclub.filmoapp.rating.client.impl.RatingClientImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static com.videoclub.filmoapp.rating.client.impl.RatingClientImpl.*;

public interface FilmService {


    FilmDTO getFilm(Long filmId);

    FilmMvcDTO getFilmForEdit (Long filmId);

    Page<FilmDTO> getFilms(String title, Pageable pageable);

    FilmDTO createFilm(FilmMvcDTO filmMvcDTO);

    FilmDTO editFilm(FilmMvcDTO filmMvcDTO);

    Integer getUserRatingForFilm(Long filmId, Long userId);

    AverageRatingResponseDTO getAverageRatingForFilm(Long filmId);

    void createRatingForFilm(Long filmId, Long userId, Integer score);

}