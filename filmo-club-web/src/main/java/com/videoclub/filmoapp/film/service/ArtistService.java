package com.videoclub.filmoapp.film.service;


import com.videoclub.filmoapp.film.domain.ArtistType;
import com.videoclub.filmoapp.film.dto.ArtistDTO;
import com.videoclub.filmoapp.film.mvc.dto.ArtistMvcDTO;

import java.util.List;

public interface ArtistService {


    List<ArtistDTO> getArtistsByType(String artistType);

    ArtistDTO createArtist(ArtistMvcDTO artistMvcDTO);

}
