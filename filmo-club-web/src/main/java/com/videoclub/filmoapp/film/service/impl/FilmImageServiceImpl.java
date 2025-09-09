package com.videoclub.filmoapp.film.service.impl;

import com.videoclub.filmoapp.film.dto.FilmImageResourceDTO;
import com.videoclub.filmoapp.film.domain.FilmImage;
import com.videoclub.filmoapp.film.service.FilmImageService;
import com.videoclub.filmoapp.core.port.StoreFacade;
import com.videoclub.filmoapp.core.dto.ResourceContentDTO;
import com.videoclub.filmoapp.core.dto.ResourceIdDTO;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FilmImageServiceImpl implements FilmImageService {

    private final StoreFacade storeFacade;

    @Override
    public FilmImage saveImage(MultipartFile multipartFile) {

        ResourceIdDTO resourceIdDTO =
                storeFacade
                        .saveResource(multipartFile, "film-poster")
                        .orElseThrow(() -> new IllegalStateException("Resource not saved in store"));

        return FilmImage.builder()
                .contentType(multipartFile.getContentType())
                .size((int) multipartFile.getSize())
                .resourceID(resourceIdDTO.getResourceId())
                .filename(multipartFile.getOriginalFilename())
                .build();
    }

    @Override
    public FilmImageResourceDTO getImage(UUID resourceId) {

        ResourceContentDTO resourceContentDTO =
                storeFacade
                        .findResource(resourceId)
                        .orElseThrow(() -> new IllegalStateException("Resource not found in store"));

        return FilmImageResourceDTO.builder()
                .content(resourceContentDTO.getContent())
                .contentType(resourceContentDTO.getContentType())
                .filename(resourceContentDTO.getFilename())
                .size(resourceContentDTO.getSize())
                .build();
    }

    @Override
    public void deleteImage(UUID resourceId) {
        storeFacade.deleteResource(resourceId);
    }
}