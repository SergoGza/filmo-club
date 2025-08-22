package com.videoclub.filmoapp.film.service.impl;


import com.videoclub.filmoapp.film.domain.Artist;
import com.videoclub.filmoapp.film.domain.ArtistType;
import com.videoclub.filmoapp.film.dto.ArtistDTO;
import com.videoclub.filmoapp.film.dto.FilmDTO;
import com.videoclub.filmoapp.film.mvc.dto.FilmMvcDTO;
import com.videoclub.filmoapp.film.repository.ArtistDAO;
import com.videoclub.filmoapp.film.repository.FilmDAO;
import com.videoclub.filmoapp.film.service.FilmService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest(
        properties = {
                "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MYSQL;DATABASE_TO_LOWER=TRUE",
                "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
                "spring.jpa.hibernate.ddl-auto:create-drop"
        })
@Transactional
class FilmServiceImplTest {

    @Autowired
    private FilmService filmService;
    @Autowired
    private FilmDAO filmDAO;
    @Autowired
    private ArtistDAO artistDAO;
    @Autowired
    private ModelMapper modelMapper;

    @BeforeEach
    void beforeEach() {

        Artist actor1 = Artist.builder()
                .name("Ronaldo")
                .surname("Nazario")
                .artistType(ArtistType.ACTOR)
                .build();

        Artist actor2 = Artist.builder()
                .name("Rivaldo")
                .surname("Nazario")
                .artistType(ArtistType.ACTOR)
                .build();

        Artist director = Artist.builder()
                .name("Roberto")
                .surname("Nazario")
                .artistType(ArtistType.DIRECTOR)
                .build();

        artistDAO.saveAll(List.of(actor1, actor2, director));


    }


    @Test
    void givenFilm_whenCreate_thenOk() {

        //Given

        List<Artist> director = artistDAO.findArtistByArtistType(ArtistType.DIRECTOR);
        ArtistDTO directorDTO = modelMapper.map(director.get(0), ArtistDTO.class);


        List<Artist> actorOrActors = artistDAO.findArtistByArtistType(ArtistType.ACTOR);

        List<ArtistDTO> actorOrActorsDTO =
                actorOrActors.stream()
                        .map(actor ->
                                modelMapper.map(actor, ArtistDTO.class)).toList();

        List<Long> actorsId = actorOrActorsDTO.stream()
                .map(ArtistDTO::getId).toList();


        FilmMvcDTO filmMvcDTO = FilmMvcDTO.builder()
                .title("El legado de los Nazario")
                .releaseYear(2025)
                .directorId(directorDTO.getId())
                .actorIds(actorsId)
                .build();


        //When

        FilmDTO filmDTO = filmService.createFilm(filmMvcDTO);


        //Then


        Assertions.assertEquals(1, filmDAO.findAll().size());
        Assertions.assertEquals(filmDTO.getTitle(), filmMvcDTO.getTitle());
        Assertions.assertEquals(filmDTO.getReleaseYear(), filmMvcDTO.getReleaseYear());

        Assertions.assertEquals(filmDTO.getDirectorId(), filmMvcDTO.getDirectorId());
        Assertions.assertEquals(filmDTO.getActorIds(), filmMvcDTO.getActorIds());



    }

}