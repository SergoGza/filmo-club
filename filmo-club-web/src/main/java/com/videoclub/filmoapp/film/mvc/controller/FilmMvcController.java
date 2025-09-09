package com.videoclub.filmoapp.film.mvc.controller;

import com.videoclub.filmoapp.auth.domain.User;
import com.videoclub.filmoapp.auth.service.UserService;
import com.videoclub.filmoapp.film.domain.ArtistType;
import com.videoclub.filmoapp.film.dto.ArtistDTO;
import com.videoclub.filmoapp.film.dto.FilmDTO;
import com.videoclub.filmoapp.film.mvc.dto.FilmMvcDTO;
import com.videoclub.filmoapp.film.service.ArtistService;
import com.videoclub.filmoapp.film.service.FilmService;
import com.videoclub.filmoapp.rating.client.impl.RatingClientImpl;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

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
    RatingClientImpl.AverageRatingResponseDTO averageRatingForFilm =
        filmService.getAverageRatingForFilm(filmId);
    Integer userRatingForFilm = filmService.getUserRatingForFilm(filmId, userId);

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("videoclub/film/film-detail");
    modelAndView.addObject("film", filmDTO);
    modelAndView.addObject("userRating", userRatingForFilm);
    modelAndView.addObject("averageRating", averageRatingForFilm.average());
    modelAndView.addObject("totalRatings", averageRatingForFilm.ratings());

    return modelAndView;
  }

  @PostMapping("/videoclub/film/rating/{filmId}")
  public Object submitRating(
      @PathVariable(name = "filmId", required = true) Long filmId,
      @RequestParam("score") Integer score,
      Authentication authentication,
      RedirectAttributes redirectAttributes) {

    try {
      String username = authentication.getName();
      User user = userService.findByUsernameOrEmail(username);
      Long userId = user.getId();
      filmService.createRatingForFilm(filmId, userId, score);
      redirectAttributes.addFlashAttribute("successMessage", "Rating submitted successfully!");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
    }
    return "redirect:/videoclub/film/films/{filmId}".formatted(filmId);
  }

  @GetMapping("/videoclub/film/search")
  public ModelAndView searchFilms() {
    return new ModelAndView("videoclub/film/search-films");
  }

  @GetMapping({"/videoclub/film/films", "/videoclub/film/films/"})
  public ModelAndView getFilms(
      @RequestParam(name = "title", required = false) String title,
      @RequestParam(name = "page", required = false, defaultValue = "0") int page,
      @RequestParam(name = "size", required = false, defaultValue = "3") int size) {
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

    FilmMvcDTO filmMvcDTO =
        Optional.ofNullable(filmId).map(filmService::getFilmForEdit).orElseGet(FilmMvcDTO::new);

    ModelAndView modelAndView = populateCreateOrEditFilmModel(filmMvcDTO, model);
    modelAndView.setViewName("videoclub/film/films-edit");

    return modelAndView;
  }

  private ModelAndView populateCreateOrEditFilmModel(FilmMvcDTO filmMvcDTO, Model model) {

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
          @RequestParam(value = "poster", required = false) MultipartFile posterFile,
          @PathVariable(name = "filmId", required = false) Long filmId,
          Model model) {

    if (filmId == null && (posterFile == null || posterFile.isEmpty())) {
      bindingResult.reject("poster.required", "Poster is required for new films");
    }

    if (bindingResult.hasErrors()) {
      ModelAndView modelAndView = populateCreateOrEditFilmModel(filmMvcDTO, model);
      modelAndView.getModel().forEach(redirectAttributes::addFlashAttribute);
      return new RedirectView("/videoclub/film/films-edit" + (filmId != null ? "/" + filmId : ""));
    }

    try {
      Optional.ofNullable(filmMvcDTO.getId())
          .map(maybeFilm -> filmService.editFilm(filmMvcDTO, posterFile))
          .orElseGet(() -> filmService.createFilm(filmMvcDTO, posterFile));
      return "redirect:/videoclub/film/films";
    } catch (Exception e) {
      log.error("Error al procesar la película", e);

      bindingResult.reject("error.global", "Error al procesar la película");

      ModelAndView modelAndView = populateCreateOrEditFilmModel(filmMvcDTO, model);
      modelAndView.setViewName("videoclub/film/films-edit");
      return modelAndView;
    }
  }
}
