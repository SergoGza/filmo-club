package com.videoclub.filmoapp.film.repository;

import com.videoclub.filmoapp.film.domain.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistDAO extends JpaRepository<Artist, Long> {
}
