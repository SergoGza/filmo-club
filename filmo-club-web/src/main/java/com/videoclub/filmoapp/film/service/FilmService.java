package com.videoclub.filmoapp.film.service;

import com.videoclub.filmoapp.film.dto.FilmDTO;
import com.videoclub.filmoapp.film.mvc.dto.FilmMvcDTO;

import java.util.List;

public interface FilmService {

    FilmDTO createFilm (FilmMvcDTO filmMvcDTO);

}
