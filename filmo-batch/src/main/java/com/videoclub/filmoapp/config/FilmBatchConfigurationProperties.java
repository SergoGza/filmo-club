package com.videoclub.filmoapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@ConfigurationProperties(prefix = "application.batch")
public record FilmBatchConfigurationProperties(Path basePath) {
  public Path getFilmBasePath() {
    return Path.of(basePath.toString(), "films.csv");
  }

  public Path getExportPath_() {
    return Paths.get(basePath.toString(), "exports", UUID.randomUUID() + ".json");
  }
}
