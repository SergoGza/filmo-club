package com.videoclub.filmoapp.film.mvc.dto;

import com.videoclub.filmoapp.film.domain.ArtistType;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilmMvcDTO {

    private Long id;
    @NotNull
    private String title;
    @NotNull
    @Digits(integer = 4, fraction = 0)
    private Integer releaseYear;
    @NotNull
    private Long directorId;
    @NotNull
    private List<Long> actorIds;

    //private String poster;

}
