package com.videoclub.filmoapp.film.mvc.controller;

import com.videoclub.filmoapp.film.dto.ArtistDTO;
import com.videoclub.filmoapp.film.mvc.dto.ArtistMvcDTO;
import com.videoclub.filmoapp.film.service.ArtistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequiredArgsConstructor
public class ArtistMvcController {

    private final ArtistService artistService;

    @GetMapping("/videoclub/artist/create")
    public ModelAndView createArtist(
            Model model
    ) {

        ArtistMvcDTO artistMvcDTO = ArtistMvcDTO.builder().build();

        ModelAndView modelAndView = new ModelAndView("videoclub/artist/artist-create");
        modelAndView.addObject("artist", artistMvcDTO);
        modelAndView.addObject("artistType", ArtistDTO.ArtistType.values());

        if (model.containsAttribute("successMessage")) {
            modelAndView.addObject("successMessage", model.getAttribute("successMessage"));
        }

        return modelAndView;

    }



    @PostMapping("/videoclub/artist/create")
    public Object createArtist(
            @Valid @ModelAttribute("artist") ArtistMvcDTO artistMvcDTO,
            RedirectAttributes redirectAttributes,
            BindingResult bindingResult
    ) {
        // 1. Verificar errores de validación
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("videoclub/artist/artist-create");
            modelAndView.addObject("artist", artistMvcDTO);
            modelAndView.addObject("artistType", ArtistDTO.ArtistType.values());
            return modelAndView;
        }

        artistService.createArtist(artistMvcDTO);

        redirectAttributes.addFlashAttribute("successMessage", "Artist has been successfully created");
        return new RedirectView("/videoclub/artist/create");


    }

}
