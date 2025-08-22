package com.videoclub.filmoapp.film.domain;


import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "films")
public class Film {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private Integer releaseYear;

    @ManyToOne
    @JoinColumn(name = "director_id")
    private Artist director;

    @ManyToMany
    @JoinTable(
            name = "films_with_artists"
            , joinColumns = @JoinColumn(name = "film_id"),
            inverseJoinColumns = @JoinColumn (name = "artist_id")
    )
    private Set<Artist> actors;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
