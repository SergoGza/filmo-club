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


}
