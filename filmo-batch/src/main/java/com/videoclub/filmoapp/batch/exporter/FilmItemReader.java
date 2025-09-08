package com.videoclub.filmoapp.batch.exporter;

import com.videoclub.filmoapp.config.FilmExportJobConfig.FilmDTO;
import com.videoclub.filmoapp.repository.FilmBatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FilmItemReader implements ItemReader<FilmDTO> {

  private final FilmBatchRepository filmBatchRepository;
  private Iterator<FilmDTO> filmIterator;
  private boolean initialized = false;

  @Override
  public FilmDTO read()
      throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

    if (!initialized) {
      List<FilmDTO> films = filmBatchRepository.findAllFilmsNotExported();
      filmIterator = films.iterator();
      initialized = true;
    }

    return filmIterator.hasNext() ? filmIterator.next() : null;
  }
}
