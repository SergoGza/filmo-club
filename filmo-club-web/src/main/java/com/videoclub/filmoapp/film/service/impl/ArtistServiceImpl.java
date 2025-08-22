package com.videoclub.filmoapp.film.service.impl;

import com.videoclub.filmoapp.film.domain.Artist;
import com.videoclub.filmoapp.film.domain.ArtistType;
import com.videoclub.filmoapp.film.dto.ArtistDTO;
import com.videoclub.filmoapp.film.mvc.dto.ArtistMvcDTO;
import com.videoclub.filmoapp.film.repository.ArtistDAO;
import com.videoclub.filmoapp.film.service.ArtistService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ArtistServiceImpl implements ArtistService {

    private final ArtistDAO artistDAO;
    private final ModelMapper modelMapper;


    @Override
    public List<ArtistDTO> getArtistsByType(String artistType) {

        List<Artist> artistsByArtistType = artistDAO.findArtistByArtistType(ArtistType.valueOf(artistType));

        return artistsByArtistType.stream().map(artist -> modelMapper.map(artist, ArtistDTO.class)).toList();

    }

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
