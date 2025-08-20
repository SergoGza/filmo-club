package com.videoclub.filmoapp.film.mvc.dto;


import com.videoclub.filmoapp.film.domain.ArtistType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtistMvcDTO {

    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String surname;

    @NotNull
    private ArtistType artistType;


}
