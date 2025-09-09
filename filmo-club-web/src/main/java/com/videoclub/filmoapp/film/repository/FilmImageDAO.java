package com.videoclub.filmoapp.film.repository;

import com.videoclub.filmoapp.film.domain.FilmImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilmImageDAO extends JpaRepository<FilmImage, Long> {}
