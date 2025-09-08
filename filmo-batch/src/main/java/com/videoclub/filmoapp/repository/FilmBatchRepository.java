package com.videoclub.filmoapp.repository;

import com.videoclub.filmoapp.config.FilmExportJobConfig;
import java.util.List;

import com.videoclub.filmoapp.config.FilmExportJobConfig.FilmDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FilmBatchRepository {

  private final JdbcTemplate jdbcTemplate;

  public List<FilmDTO> findAllFilms() {

    return jdbcTemplate.query(
"""
  SELECT id, title, release_year FROM films
""",
        (rs, rowNum) ->
            new FilmDTO(rs.getLong("id"), rs.getString("title"), rs.getInt("release_year")));
  }

  public List<FilmDTO> findAllFilmsNotExported() {
    return jdbcTemplate.query(
"""
  SELECT id, title, release_year FROM films f1
  WHERE NOT EXISTS (select 1 from film_export f2 where f1.id = f2.film_id)
""",
        (rs, rowNum) ->
            new FilmDTO(rs.getLong("id"), rs.getString("title"), rs.getInt("release_year")));
  }
}
