package com.videoclub.filmoapp.film.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilmDTO {

    private Long id;
    private String title;
    private Integer releaseYear;
    private Long directorId;
    private List<Long> actorIds;
    //private String poster;


}
