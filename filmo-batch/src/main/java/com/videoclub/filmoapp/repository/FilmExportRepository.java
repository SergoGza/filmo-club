package com.videoclub.filmoapp.repository;

import com.videoclub.filmoapp.domain.FilmExport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilmExportRepository extends JpaRepository<FilmExport, Long> {}
