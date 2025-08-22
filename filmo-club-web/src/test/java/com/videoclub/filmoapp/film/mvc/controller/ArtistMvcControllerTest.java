package com.videoclub.filmoapp.film.mvc.controller;

import com.videoclub.filmoapp.film.domain.ArtistType;
import com.videoclub.filmoapp.film.mvc.dto.ArtistMvcDTO;
import com.videoclub.filmoapp.film.service.ArtistService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ArtistMvcController.class)
class ArtistMvcControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArtistService artistService;


    @Test
    void givenCreateArtistView_whenGet_thenOk() throws Exception {

        mockMvc.perform(
                        get("/videoclub/artist/create"))
                .andExpect(
                        status().isOk())
                .andExpect(view().name("videoclub/artist/artist-create"))
                .andExpect(model().attributeExists("artist"))
                .andExpect(model().attribute("artistType", ArtistType.values()));

    }

    @Test
    void givenCreateArtistView_whenPost_thenOk() throws Exception {

        ArtistMvcDTO artistMvcDTO = ArtistMvcDTO.builder()
                .name("Rivaldo")
                .surname("Nazario")
                .artistType(ArtistType.ACTOR)
                .build();

        mockMvc.perform(post(
                        "/videoclub/artist/create"
                )
                        .flashAttr("artist", artistMvcDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/videoclub/artist/create"));

        Mockito.verify(artistService, Mockito.times(1)).createArtist(artistMvcDTO);

    }


}