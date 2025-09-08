package com.videoclub.filmoapp.batch.exporter;

import com.videoclub.filmoapp.config.FilmExportJobConfig.FilmDTO;
import com.videoclub.filmoapp.domain.FilmExport;
import com.videoclub.filmoapp.repository.FilmExportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@StepScope
public class FilmExportItemWriter implements ItemWriter<FilmDTO> {

  private final FilmExportRepository filmExportRepository;

  @Value("#{stepExecution.jobExecution.id}")
  private Long jobId;

  @Override
  public void write(Chunk<? extends FilmDTO> chunk) throws Exception {
    for (FilmDTO film : chunk) {
      FilmExport filmExport = FilmExport.create(film.id(), jobId);
      filmExportRepository.save(filmExport);
    }
  }
}
