package com.videoclub.filmoapp.film.service;

import com.videoclub.filmoapp.film.dto.FilmDTO;
import com.videoclub.filmoapp.film.mvc.dto.FilmMvcDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FilmService {

    FilmDTO createFilm (FilmMvcDTO filmMvcDTO);

    Page<FilmDTO> getFilms (String title, Pageable pageable);

}
