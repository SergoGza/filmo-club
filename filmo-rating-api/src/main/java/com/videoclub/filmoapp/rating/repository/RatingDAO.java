package com.videoclub.filmoapp.rating.repository;

import com.videoclub.filmoapp.rating.domain.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingDAO extends JpaRepository<Rating, Long> {

    List<Rating> findByFilmId(Long filmId);
    Optional<Rating> findByFilmIdAndUserId(Long filmId, Long userId);
    boolean existsByFilmIdAndUserId(Long filmId, Long userId);

}
