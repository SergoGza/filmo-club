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
@Table(name = "artists")
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String surname;

    private ArtistType artistType;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "artists_with_films",
            joinColumns = @JoinColumn(name = "film_id"),
            inverseJoinColumns = @JoinColumn(name = "artist_id")
    )
    private Set<Film> films;

}
