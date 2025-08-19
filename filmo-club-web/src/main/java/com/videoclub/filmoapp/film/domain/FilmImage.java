package com.videoclub.filmoapp.film.domain;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "film_image")
public class FilmImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "resource_id")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID resourceId;

    private String filename;
    private String contentType;
    private int size;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "film_id")
    private Film film;

}
