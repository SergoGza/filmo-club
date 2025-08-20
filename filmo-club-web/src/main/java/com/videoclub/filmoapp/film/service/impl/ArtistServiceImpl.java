package com.videoclub.filmoapp.film.service.impl;

import com.videoclub.filmoapp.film.domain.Artist;
import com.videoclub.filmoapp.film.domain.ArtistType;
import com.videoclub.filmoapp.film.dto.ArtistDTO;
import com.videoclub.filmoapp.film.mvc.dto.ArtistMvcDTO;
import com.videoclub.filmoapp.film.repository.ArtistDAO;
import com.videoclub.filmoapp.film.service.ArtistService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

@Service
@RequiredArgsConstructor
public class ArtistServiceImpl implements ArtistService {

    private final ArtistDAO artistDAO;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public ArtistDTO createArtist(ArtistMvcDTO artistMvcDTO) {

        if (artistDAO.existsByNameAndSurnameAndArtistType(artistMvcDTO.getName(), artistMvcDTO.getSurname(), artistMvcDTO.getArtistType())) {
            throw new IllegalArgumentException("Artist already exists");
        }

        Artist artist = Artist.builder()
                .name(artistMvcDTO.getName())
                .surname(artistMvcDTO.getSurname())
                .artistType(artistMvcDTO.getArtistType())
                .build();

        Artist artistSaved = artistDAO.save(artist);

        return modelMapper.map(artistSaved, ArtistDTO.class);

    }


}
