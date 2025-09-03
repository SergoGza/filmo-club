package com.videoclub.filmoapp.rating.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "ratings",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"film_id", "user_id"})})
public class Rating {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "film_id")
  @NotNull
  private Long filmId;

  @Column(name = "user_id")
  @NotNull
  private Long userId;

  @NotNull
  @Min(1)
  @Max(5)
  private Integer score;
}
