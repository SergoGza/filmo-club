package com.videoclub.filmoapp.film.mvc.controller;

import com.videoclub.filmoapp.film.dto.FilmImageResourceDTO;
import com.videoclub.filmoapp.film.service.FilmImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class FilmResourceMvcController {

  private final FilmImageService filmImageService;

  @GetMapping("/videoclub/resources/{resourceId}")
  public ResponseEntity<byte[]> getFilmResourceAsStream(
      @PathVariable(value = "resourceId") UUID resourceId) {

    FilmImageResourceDTO filmImageResourceDTO = filmImageService.getImage(resourceId);

    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(filmImageResourceDTO.getContentType()))
        .contentLength(filmImageResourceDTO.getSize())
        .body(filmImageResourceDTO.getContent());
  }

  @GetMapping("/videoclub/downloads/{resourceId}")
  public ResponseEntity<byte[]> getFilmResourceAsAttachment(
      @PathVariable(value = "resourceId") UUID resourceId) {

    FilmImageResourceDTO filmImageResourceDTO = filmImageService.getImage(resourceId);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add(
        HttpHeaders.CONTENT_DISPOSITION,
        "attachment; filename=" + filmImageResourceDTO.getFilename());
    httpHeaders.add(HttpHeaders.CONTENT_TYPE, filmImageResourceDTO.getContentType());
    httpHeaders.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(filmImageResourceDTO.getSize()));

    return ResponseEntity.ok().headers(httpHeaders).body(filmImageResourceDTO.getContent());
  }
}
