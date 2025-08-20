package com.videoclub.filmoapp.film.repository;

import com.videoclub.filmoapp.film.domain.Artist;
import com.videoclub.filmoapp.film.domain.ArtistType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistDAO extends JpaRepository<Artist, Long> {



   boolean existsByNameAndSurnameAndArtistType(String artistName, String surname, ArtistType artistType);

}
