package com.videoclub.filmoapp.infrastructure.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.videoclub.filmoapp.core.port.StoreFacade;
import com.videoclub.filmoapp.core.config.FilmResourceConfigurationProperties;
import com.videoclub.filmoapp.core.dto.ResourceContentDTO;
import com.videoclub.filmoapp.core.dto.ResourceIdDTO;
import jakarta.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocalStoreFacadeImpl implements StoreFacade {

    private final FilmResourceConfigurationProperties filmResourceConfigurationProperties;
    private final ObjectMapper objectMapper;

    @Override
    public Optional<ResourceIdDTO> saveResource(MultipartFile multipartFile, @Nullable String description) {
        log.info("GUARDANDO RECURSO: {} en {}", multipartFile.getOriginalFilename(), filmResourceConfigurationProperties.basePath());


        ResourceDescription resourceDescription =
                ResourceDescription.builder()
                        .description(description)
                        .contentType(multipartFile.getContentType())
                        .filename(multipartFile.getOriginalFilename())
                        .size((int) multipartFile.getSize())
                        .build();

        ResourceIdDTO resourceIdDTO = ResourceIdDTO.builder().resourceId(UUID.randomUUID()).build();

        Path pathToContent =
                filmResourceConfigurationProperties.getResourcePath(resourceIdDTO.getResourceId().toString());
        Path pathToDescription =
                filmResourceConfigurationProperties.getResourcePath(resourceIdDTO.getResourceId() + ".json");

        try {
            // Crear directorios si no existen
            Files.createDirectories(pathToContent.getParent());

            Files.write(pathToContent, multipartFile.getBytes());
        } catch (IOException ioe) {
            log.error("Exception in saveResource", ioe);
            return Optional.empty();
        }

        try {
            objectMapper.writeValue(pathToDescription.toFile(), resourceDescription);
        } catch (IOException ioe) {
            log.error("Exception in saveResource", ioe);
            return Optional.empty();
        }

        return Optional.of(resourceIdDTO);
    }

    @Override
    public Optional<ResourceContentDTO> findResource(UUID resourceId) {

        Path pathToContent = filmResourceConfigurationProperties.getResourcePath(resourceId.toString());
        Path pathToDescription = filmResourceConfigurationProperties.getResourcePath(resourceId + ".json");

        if (!Files.exists(pathToContent)) {
            return Optional.empty();
        }

        byte[] bytes;
        try {
            bytes = Files.readAllBytes(pathToContent);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }

        ResourceDescription resourceDescription;
        try {
            resourceDescription =
                    objectMapper.readValue(pathToDescription.toFile(), ResourceDescription.class);
        } catch (IOException ioe) {
            log.error("Exception in findResource", ioe);
            return Optional.empty();
        }

        return Optional.of(
                ResourceContentDTO.builder()
                        .resourceId(resourceId)
                        .content(bytes)
                        .contentType(resourceDescription.getContentType())
                        .description(resourceDescription.getDescription())
                        .filename(resourceDescription.getFilename())
                        .size(resourceDescription.getSize())
                        .build());
    }

    @Override
    public void deleteResource(UUID resourceID) {

        Path pathFromContent = filmResourceConfigurationProperties.getResourcePath(resourceID.toString());
        Path pathFromDescription = filmResourceConfigurationProperties.getResourcePath(resourceID + ".json");

        try {
            Files.deleteIfExists(pathFromContent);
            Files.deleteIfExists(pathFromDescription);
        } catch (IOException ioe) {
            log.error("Exception in deleteResource", ioe);
        }
    }

    // Clase interna para metadatos (igual que en StoreServiceImpl)
    @lombok.Builder
    @lombok.Value
    private static class ResourceDescription {
        String contentType;
        String filename;
        String description;
        int size;
    }
}