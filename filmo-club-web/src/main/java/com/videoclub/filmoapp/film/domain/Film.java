package com.videoclub.filmoapp.film.domain;


import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "films")
public class Film {


    @Id
    private Long id;

    private String title;

    private Integer releaseYear;




    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
