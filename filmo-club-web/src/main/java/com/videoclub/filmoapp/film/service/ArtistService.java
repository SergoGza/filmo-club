package com.videoclub.filmoapp.film.service;


import com.videoclub.filmoapp.film.domain.ArtistType;
import com.videoclub.filmoapp.film.dto.ArtistDTO;
import com.videoclub.filmoapp.film.mvc.dto.ArtistMvcDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface ArtistService {


    List<ArtistDTO> getArtistsByType(String artistType);

//    @PreAuthorize(value = "hasRole('ADMIN')")
    ArtistDTO createArtist(ArtistMvcDTO artistMvcDTO);

}
