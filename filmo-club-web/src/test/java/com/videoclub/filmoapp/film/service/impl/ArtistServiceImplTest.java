package com.videoclub.filmoapp.film.service.impl;

import com.videoclub.filmoapp.film.domain.Artist;
import com.videoclub.filmoapp.film.domain.ArtistType;
import com.videoclub.filmoapp.film.dto.ArtistDTO;
import com.videoclub.filmoapp.film.mvc.dto.ArtistMvcDTO;
import com.videoclub.filmoapp.film.repository.ArtistDAO;
import com.videoclub.filmoapp.film.service.ArtistService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest(
        properties = {
                "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MYSQL;DATABASE_TO_LOWER=TRUE",
                "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
                "spring.jpa.hibernate.ddl-auto:create-drop"
        })
@Transactional
class ArtistServiceImplTest {

    @Autowired
    private ArtistDAO artistDAO;
    @Autowired
    private ArtistService artistService;

    @BeforeEach
    void beforeEach() {

        Artist artist = Artist.builder()
                .name("Ronaldo")
                .artistType(ArtistType.ACTOR)
                .surname("Nazario")
                .films(null)
                .build();

        artistDAO.save(artist);

    }


    @Test
    void givenArtist_whenCreate_thenOk() {

        //Given

        ArtistMvcDTO artistMvcDTO =
                ArtistMvcDTO.builder()
                        .name("Rivaldo")
                        .surname("Nazario")
                        .artistType(ArtistType.ACTOR)
                        .build();

        Assertions.assertEquals(1, artistDAO.findAll().size());

        //When

        ArtistDTO createdArtist = artistService.createArtist(artistMvcDTO);


        //Then

        Assertions.assertEquals(2, artistDAO.findAll().size());


        Assertions.assertEquals(artistMvcDTO.getName(), createdArtist.getName());
        Assertions.assertEquals(artistMvcDTO.getSurname(), createdArtist.getSurname());
        Assertions.assertEquals(artistMvcDTO.getArtistType().name(), createdArtist.getArtistType().name());


    }


}