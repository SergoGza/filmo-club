package com.videoclub.filmoapp.film.mvc.controller;


import com.videoclub.filmoapp.film.domain.ArtistType;
import com.videoclub.filmoapp.film.dto.ArtistDTO;
import com.videoclub.filmoapp.film.dto.FilmDTO;
import com.videoclub.filmoapp.film.mvc.dto.FilmMvcDTO;
import com.videoclub.filmoapp.film.service.ArtistService;
import com.videoclub.filmoapp.film.service.FilmService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class FilmMvcController {

    private final FilmService filmService;
    private final ArtistService artistService;


    @GetMapping("/videoclub/film/search")
    public ModelAndView searchFilms() {
        return new ModelAndView("videoclub/film/search-films");
    }


    @GetMapping("/videoclub/film/films")
    public ModelAndView getFilms (
        @RequestParam (name = "title", required = false) String title,
        @RequestParam (name = "page", required = false, defaultValue = "0") int page,
        @RequestParam (name = "size", required = false, defaultValue = "2") int size
    ) {

        Pageable pageable = PageRequest.of(page, size);
        Page<FilmDTO> filmDTOPage = filmService.getFilms(title, pageable);

        ModelAndView modelAndView = new ModelAndView("videoclub/film/films");
        modelAndView.addObject("title", title);
        modelAndView.addObject("filmDTOPage", filmDTOPage);

        return modelAndView;

    }

    @GetMapping("/videoclub/film/create")
    public ModelAndView createFilm(
            Model model
    ) {


        List<ArtistDTO> directors = artistService.getArtistsByType(String.valueOf(ArtistType.DIRECTOR));
        List<ArtistDTO> actors = artistService.getArtistsByType(String.valueOf(ArtistType.ACTOR));

        FilmMvcDTO filmMvcDTO = new FilmMvcDTO();


        ModelAndView modelAndView = new ModelAndView("videoclub/film/film-create");
        modelAndView.addObject("film", filmMvcDTO);
        modelAndView.addObject("director", directors);
        modelAndView.addObject("actors", actors);

        if (model.containsAttribute("successMessage")) {
            modelAndView.addObject("successMessage", model.getAttribute("successMessage"));
        }

        return modelAndView;

    }

    @PostMapping("/videoclub/film/create")
    public Object createArtist(
            @Valid @ModelAttribute("film") FilmMvcDTO filmMvcDTO,
            RedirectAttributes redirectAttributes,
            BindingResult bindingResult
    ) {

        List<ArtistDTO> directors = artistService.getArtistsByType(String.valueOf(ArtistType.DIRECTOR));
        List<ArtistDTO> actors = artistService.getArtistsByType(String.valueOf(ArtistType.ACTOR));

        // 1. Verificar errores de validación

        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("videoclub/film/film-create");
            modelAndView.addObject("film", filmMvcDTO);
            modelAndView.addObject("director", directors);
            modelAndView.addObject("actors", actors);
            return modelAndView;
        }

        filmService.createFilm(filmMvcDTO);

        redirectAttributes.addFlashAttribute("succesMessage", "Film has been successfully created");
        return new RedirectView("/videoclub/film/create");

    }
}
