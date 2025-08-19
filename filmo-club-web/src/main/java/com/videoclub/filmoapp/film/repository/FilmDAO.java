package com.videoclub.filmoapp.film.repository;

import com.videoclub.filmoapp.film.domain.Film;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FilmDAO extends JpaRepository<Film, Long> {


    List<Film> findByTitleLike(String title);

}
