package com.videoclub.filmoapp.film.dto;

import com.videoclub.filmoapp.core.dto.ResourceDTO;
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
    private String directorName;
    private List<String> actorNames;
    private ResourceDTO image;



}
