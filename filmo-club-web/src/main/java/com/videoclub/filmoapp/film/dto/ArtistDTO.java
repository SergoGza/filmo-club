package com.videoclub.filmoapp.film.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistDTO {

    private Long id;
    private String name;
    private String surname;
    private ArtistType artistType;

    public enum ArtistType {
        ACTOR,
        DIRECTOR
    }

}
