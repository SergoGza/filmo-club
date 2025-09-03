package com.videoclub.filmoapp.film.mvc.controller;

import com.videoclub.filmoapp.auth.domain.User;
import com.videoclub.filmoapp.auth.dto.UserDTO;
import com.videoclub.filmoapp.auth.service.UserService;
import com.videoclub.filmoapp.film.domain.ArtistType;
import com.videoclub.filmoapp.film.dto.ArtistDTO;
import com.videoclub.filmoapp.film.dto.FilmDTO;
import com.videoclub.filmoapp.film.mvc.dto.FilmMvcDTO;
import com.videoclub.filmoapp.film.service.ArtistService;
import com.videoclub.filmoapp.film.service.FilmService;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class FilmMvcController {

  private final FilmService filmService;
  private final ArtistService artistService;
  private final UserService userService;

  @GetMapping({"/videoclub/film/films/{filmId}", "/videoclub/film/films/{filmId}/"})
  public ModelAndView getFilmDetail(
      @PathVariable(name = "filmId", required = true) Long filmId, Authentication authentication) {

    String username = authentication.getName();
    User user = userService.findByUsernameOrEmail(username);
    Long userId = user.getId();

    FilmDTO filmDTO = filmService.getFilm(filmId);

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("videoclub/film/film-detail");
    modelAndView.addObject("film", filmDTO);

    return modelAndView;
  }

  @GetMapping("/videoclub/film/search")
  public ModelAndView searchFilms() {
    return new ModelAndView("videoclub/film/search-films");
  }

  @GetMapping({"/videoclub/film/films", "/videoclub/film/films/"})
  public ModelAndView getFilms(
      @RequestParam(name = "title", required = false) String title,
      @RequestParam(name = "page", required = false, defaultValue = "0") int page,
      @RequestParam(name = "size", required = false, defaultValue = "4") int size) {
    Pageable pageable = PageRequest.of(page, size);

    Page<FilmDTO> films = filmService.getFilms(title, pageable);

    ModelAndView modelAndView = new ModelAndView("videoclub/film/films");
    modelAndView.addObject("films", films);
    modelAndView.addObject("title", title);

    return modelAndView;
  }

  @GetMapping({
    "/videoclub/film/films-edit",
    "/videoclub/film/films-edit/",
    "/videoclub/film/films-edit/{filmId}"
  })
  public ModelAndView createOrEditFilm(
      @PathVariable(name = "filmId", required = false) Long filmId, Model model) {

    Optional<FilmDTO> maybeFilmDTO = Optional.ofNullable(filmId).map(filmService::getFilm);

    FilmMvcDTO filmMvcDTO =
        maybeFilmDTO
            .map(
                filmDTO ->
                    FilmMvcDTO.builder()
                        .id(filmDTO.getId())
                        .title(filmDTO.getTitle())
                        .releaseYear(filmDTO.getReleaseYear())
                        .actorIds(filmDTO.getActorIds())
                        .directorId(filmDTO.getDirectorId())
                        .build())
            .orElseGet(FilmMvcDTO::new);

    ModelAndView modelAndView =
        populateCreateOrEditFilmModel(filmMvcDTO, maybeFilmDTO.orElse(null), model);
    modelAndView.setViewName("videoclub/film/films-edit");

    return modelAndView;
  }

  private ModelAndView populateCreateOrEditFilmModel(
      FilmMvcDTO filmMvcDTO, @Nullable FilmDTO filmDTO, Model model) {

    List<ArtistDTO> directors = artistService.getArtistsByType(String.valueOf(ArtistType.DIRECTOR));
    List<ArtistDTO> actors = artistService.getArtistsByType(String.valueOf(ArtistType.ACTOR));

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.addAllObjects(model.asMap());
    if (!model.containsAttribute("film")) {
      modelAndView.addObject("film", filmMvcDTO);
    }
    if (model.containsAttribute("successMessage")) {
      modelAndView.addObject("successMessage", model.getAttribute("successMessage"));
    }

    modelAndView.addObject("directors", directors);
    modelAndView.addObject("actors", actors);

    return modelAndView;
  }

  @PostMapping({
    "/videoclub/film/films-edit",
    "/videoclub/film/films-edit/",
    "/videoclub/film/films-edit/{filmId}"
  })
  public Object createOrEditFilmPost(
      @Valid @ModelAttribute("film") FilmMvcDTO filmMvcDTO,
      RedirectAttributes redirectAttributes,
      BindingResult bindingResult,
      @PathVariable(name = "filmId", required = false) Long filmId,
      Model model) {

    if (bindingResult.hasErrors()) {

      Optional<FilmDTO> maybeFilmDTO = Optional.ofNullable(filmId).map(filmService::getFilm);

      ModelAndView modelAndView =
          populateCreateOrEditFilmModel(filmMvcDTO, maybeFilmDTO.orElse(null), model);

      modelAndView.getModel().forEach(redirectAttributes::addFlashAttribute);
      return new RedirectView("videoclub/film/films-edit" + (filmId != null ? "/" + filmId : ""));
    }

    try {
      Optional.ofNullable(filmMvcDTO.getId())
          .map(maybeFilm -> filmService.editFilm(filmMvcDTO))
          .orElseGet(() -> filmService.createFilm(filmMvcDTO));
      return "redirect:/videoclub/film/films";
    } catch (Exception e) {
      log.error("Error al procesar la película", e);

      bindingResult.reject("error.global", "Error al procesar la película");

      FilmDTO filmDTO = null;
      if (filmMvcDTO.getId() != null) {
        try {
          filmDTO = filmService.getFilm(filmMvcDTO.getId());
        } catch (Exception ex) {
          log.error("Error al obtener la película con id: {}", filmMvcDTO.getId(), ex);
        }
      }

      ModelAndView modelAndView = populateCreateOrEditFilmModel(filmMvcDTO, filmDTO, model);
      modelAndView.setViewName("videoclub/film/films-edit");
      return modelAndView;
    }
  }
}
