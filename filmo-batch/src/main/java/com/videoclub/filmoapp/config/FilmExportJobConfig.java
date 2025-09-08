package com.videoclub.filmoapp.config;

import com.videoclub.filmoapp.batch.exporter.FilmCsvItemWriter;
import com.videoclub.filmoapp.batch.exporter.FilmExportItemWriter;
import com.videoclub.filmoapp.batch.exporter.FilmItemReader;
import com.videoclub.filmoapp.repository.FilmBatchRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Configuración básica de Spring Batch
 *
 * <p>¿Qué hace esta configuración? - Define un Job llamado "filmExportJob" - El Job tiene un Step
 * que simplemente imprime un mensaje - Esta es la configuración mínima para verificar que Spring
 * Batch funciona
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class FilmExportJobConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager platformTransactionManager;
  private final FilmBatchRepository filmBatchRepository;
  private final FilmItemReader filmItemReader;
  private final FilmExportItemWriter filmExportItemWriter;
  private final FilmCsvItemWriter filmCsvItemWriter;

  /**
   * Define el Job principal de exportación de películas Por ahora solo tiene un step simple para
   * verificar funcionamiento
   */
  @Bean
  public Job filmExportJob() {
    return new JobBuilder("filmExportJob", jobRepository)
        .start(testStep())
        .next(countFilmsStep())
        .next(readFilmStep())
        .build();
  }

  /**
   * Step de prueba que simplemente imprime un mensaje Un Tasklet es la forma más simple de Step:
   * ejecuta una tarea y termina
   */
  @Bean
  public Step testStep() {
    return new StepBuilder("testStep", jobRepository)
        .tasklet(
            (contribution, chunkContext) -> {
              log.info("🎬 Spring Batch está funcionando correctamente!");
              log.info("📊 Configuración básica completada");
              return RepeatStatus.FINISHED;
            },
            platformTransactionManager)
        .build();
  }

  @Bean
  public Step countFilmsStep() {
    return new StepBuilder("countFilmsStep", jobRepository)
        .tasklet(
            (contribution, chunkContext) -> {
              List<FilmDTO> allFilms = filmBatchRepository.findAllFilms();
              List<FilmDTO> allFilmsNotExported = filmBatchRepository.findAllFilmsNotExported();
              log.info("Total films found: {}", allFilms.size());
              log.info("Total films not exported found: {}", allFilmsNotExported.size());

              return RepeatStatus.FINISHED;
            },
            platformTransactionManager)
        .build();
  }

  @Bean
  public Step readFilmStep() {
    return new StepBuilder("readFilmStep", jobRepository)
        .<FilmDTO, FilmDTO>chunk(3, platformTransactionManager)
        .reader(filmItemReader)
        .writer(compositeItemWriter())
        .build();
  }

  @Bean
  public CompositeItemWriter<FilmDTO> compositeItemWriter() {
    CompositeItemWriter<FilmDTO> compositeItemWriter = new CompositeItemWriter<>();
    compositeItemWriter.setDelegates(List.of(filmExportItemWriter, filmCsvItemWriter));

    return compositeItemWriter;
  }

  public record FilmDTO(Long id, String title, Integer releaseYear) {}
}
