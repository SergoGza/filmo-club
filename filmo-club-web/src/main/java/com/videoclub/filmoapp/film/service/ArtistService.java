package com.videoclub.filmoapp.film.service;


import com.videoclub.filmoapp.film.dto.ArtistDTO;
import com.videoclub.filmoapp.film.mvc.dto.ArtistMvcDTO;

public interface ArtistService {

ArtistDTO createArtist (ArtistMvcDTO artistMvcDTO);

}
