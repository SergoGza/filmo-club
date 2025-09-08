package com.videoclub.filmoapp.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entidad que registra qué películas han sido exportadas
 *
 * ¿Para qué sirve?
 * - Lleva un registro de todas las películas que ya se exportaron
 * - Evita duplicados en futuras ejecuciones del batch
 * - Permite hacer exportaciones incrementales
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "film_export")
public class FilmExport {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;


  @Column(name = "film_id", nullable = false)
  private Long filmId;

  @Column(name = "job_id", nullable = false)
  private Long jobId; // Cambio: Long en lugar de Integer para compatibilidad

  @Column(name = "exported_at", nullable = false)
  private LocalDateTime exportedAt;


  /**
   * Constructor de conveniencia para crear nuevos registros de exportación
   */
  public static FilmExport create(Long filmId, Long jobId) {
    return FilmExport.builder()
            .filmId(filmId)
            .jobId(jobId)
            .exportedAt(LocalDateTime.now())
            .build();
  }
}