package com.videoclub.filmoapp.film.service.impl;

import com.videoclub.filmoapp.film.domain.Artist;
import com.videoclub.filmoapp.film.domain.Film;
import com.videoclub.filmoapp.film.dto.FilmDTO;
import com.videoclub.filmoapp.film.mvc.dto.FilmMvcDTO;
import com.videoclub.filmoapp.film.repository.ArtistDAO;
import com.videoclub.filmoapp.film.repository.FilmDAO;
import com.videoclub.filmoapp.film.service.FilmService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {

    private final FilmDAO filmDAO;
    private final ArtistDAO artistDAO;
    private final ModelMapper modelMapper;

    @Override
    public Page<FilmDTO> getFilms(String title, Pageable pageable) {

        Page<Film> filmsByTitleLike = filmDAO.findByTitleLike(title, pageable);
        return filmsByTitleLike.map(film -> modelMapper.map(film, FilmDTO.class));

    }

    @Override
    @Transactional
    public FilmDTO createFilm(FilmMvcDTO filmMvcDTO) {

        // condicional para comprobar que no existe una película

        Artist director = artistDAO.findById(filmMvcDTO.getDirectorId())
                .orElseThrow(() -> new IllegalArgumentException("Director not found"));

        Set<Artist> actorOrActors = new HashSet<>(
                Optional.ofNullable(filmMvcDTO.getActorIds())
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(id -> artistDAO.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Actor with id:%s not found ".formatted(id))))
                        .toList());


        Film film = Film.builder()
                .title(filmMvcDTO.getTitle())
                .releaseYear(filmMvcDTO.getReleaseYear())
                .director(director)
                .actors(actorOrActors)
                .build();

        Film filmSaved = filmDAO.save(film);

        return FilmDTO.builder()
                .title(filmSaved.getTitle())
                .releaseYear(filmSaved.getReleaseYear())
                .directorId(director.getId())
                .actorIds(
                        actorOrActors.stream().
                                map(Artist::getId)
                                .sorted()
                                .collect(Collectors.toList()))
                .build();


    }
}
