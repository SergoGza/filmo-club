package com.videoclub.filmoapp.batch.exporter;

import com.videoclub.filmoapp.config.FilmBatchConfigurationProperties;
import com.videoclub.filmoapp.config.FilmExportJobConfig.FilmDTO;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FilmCsvItemWriter implements ItemWriter<FilmDTO> {

  private final FilmBatchConfigurationProperties properties;

  @Override
  public void write(Chunk<? extends FilmDTO> chunk) throws Exception {
    Path csvPath = properties.getFilmBasePath();

    log.info("Escribiendo {} películas al archivo: {}", chunk.size(), csvPath);

    Files.createDirectories(csvPath.getParent());

    try (FileWriter writer = new FileWriter(csvPath.toFile(), true)) {
      for (FilmDTO film : chunk) {
        String csvLine = String.format("%d,%s,%d%n", film.id(), film.title(), film.releaseYear());
        writer.write(csvLine);
        log.info("Escrita línea: {}", csvLine.trim());
      }
    }
  }
}
