package com.videoclub.filmoapp.film.service;

import com.videoclub.filmoapp.film.dto.FilmImageResourceDTO;
import com.videoclub.filmoapp.film.domain.FilmImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface FilmImageService {

    FilmImage saveImage(MultipartFile multipartFile);
    FilmImageResourceDTO getImage(UUID resourceId);
    void deleteImage(UUID resourceId);
}