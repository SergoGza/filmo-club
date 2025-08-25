package com.videoclub.filmoapp.film.repository;

import com.videoclub.filmoapp.film.domain.Film;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FilmDAO extends JpaRepository<Film, Long> {


    @Query("""
    SELECT f 
    FROM Film f
    WHERE f.title LIKE CONCAT('%', :title, '%')
    ORDER BY 
      CASE
        WHEN f.title = :title THEN 1
        WHEN f.title LIKE CONCAT(:title, '%') THEN 2
        ELSE 3
      END,
      f.title ASC
""")
    Page<Film> findByTitleLike(@Param("title") String title, Pageable pageable);

}
