package com.videoclub.filmoapp.core.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

@ConfigurationProperties(prefix = "application.film.resources")
public record FilmResourceConfigurationProperties (Path basePath){

    public Path getResourcePath(String filename) {
        return Path.of(basePath.toString(), filename);
    }

}
