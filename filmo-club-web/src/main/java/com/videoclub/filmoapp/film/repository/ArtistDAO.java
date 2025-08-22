package com.videoclub.filmoapp.film.repository;

import com.videoclub.filmoapp.film.domain.Artist;
import com.videoclub.filmoapp.film.domain.ArtistType;
import com.videoclub.filmoapp.film.dto.ArtistDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArtistDAO extends JpaRepository<Artist, Long> {



   boolean existsByNameAndSurnameAndArtistType(String artistName, String surname, ArtistType artistType);

    List<Artist> findArtistByArtistType(ArtistType artistType);
}
