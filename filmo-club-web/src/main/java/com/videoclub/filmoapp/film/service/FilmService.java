package com.videoclub.filmoapp.film.service;

import com.videoclub.filmoapp.film.dto.FilmDTO;
import com.videoclub.filmoapp.film.mvc.dto.FilmMvcDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FilmService {


    FilmDTO getFilm(Long filmId);

    Page<FilmDTO> getFilms(String title, Pageable pageable);

    FilmDTO createFilm(FilmMvcDTO filmMvcDTO);

    FilmDTO editFilm(FilmMvcDTO filmMvcDTO);

}