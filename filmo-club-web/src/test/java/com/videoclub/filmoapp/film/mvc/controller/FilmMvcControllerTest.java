package com.videoclub.filmoapp.film.mvc.controller;

import com.videoclub.filmoapp.film.domain.Artist;
import com.videoclub.filmoapp.film.domain.ArtistType;
import com.videoclub.filmoapp.film.dto.ArtistDTO;
import com.videoclub.filmoapp.film.mvc.dto.FilmMvcDTO;
import com.videoclub.filmoapp.film.service.ArtistService;
import com.videoclub.filmoapp.film.service.FilmService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(FilmMvcController.class)
class FilmMvcControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FilmService filmService;

    @MockBean
    private ArtistService artistService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public ModelMapper modelMapper() {
            return new ModelMapper();
        }
    }

    @Autowired
    private ModelMapper modelMapper;


    @Test
    void givenCreateFilm_whenGet_thenOK() throws Exception {


        mockMvc.perform(
                        MockMvcRequestBuilders.get("/videoclub/film/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("videoclub/film/film-create"))
                .andExpect(model().attributeExists("film"))
                .andExpect(model().attribute("actors", artistService.getArtistsByType(String.valueOf(ArtistType.ACTOR))));
    }


    @Test
    void givenCreateFilm_whenPost_thenOK() throws Exception {


        Artist actor1 = Artist.builder()
                .id(1L)
                .name("Ronaldo")
                .surname("Nazario")
                .artistType(ArtistType.ACTOR)
                .build();

        Artist actor2 = Artist.builder()
                .id(2L)
                .name("Rivaldo")
                .surname("Nazario")
                .artistType(ArtistType.ACTOR)
                .build();

        Artist director = Artist.builder()
                .id(3L)
                .name("Roberto")
                .surname("Nazario")
                .artistType(ArtistType.DIRECTOR)
                .build();

        List<Artist> actors = Arrays.asList(actor1, actor2);
        List<Artist> directors = Arrays.asList(director);

        List<ArtistDTO> actorsDTO = actors.stream().map(actor -> modelMapper.map(actor, ArtistDTO.class)).toList();
        List<ArtistDTO> directorsDTO = directors.stream().map(d -> modelMapper.map(d, ArtistDTO.class)).toList();


        Mockito.when(artistService.getArtistsByType(String.valueOf(ArtistType.ACTOR))).thenReturn(actorsDTO);
        Mockito.when(artistService.getArtistsByType(String.valueOf(ArtistType.DIRECTOR))).thenReturn(directorsDTO);



        FilmMvcDTO filmMvcDTO = FilmMvcDTO.builder()
                .title("El legado de los Nazario")
                .releaseYear(2025)
                .directorId(directorsDTO.get(0).getId())
                .actorIds(actorsDTO.stream().map(ArtistDTO::getId).collect(Collectors.toList()))
                .build();


        mockMvc.perform(MockMvcRequestBuilders.post("/videoclub/film/create")
                        .flashAttr("film", filmMvcDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/videoclub/film/create"));


        Mockito.verify(filmService, Mockito.times(1)).createFilm(filmMvcDTO);
    }

}

