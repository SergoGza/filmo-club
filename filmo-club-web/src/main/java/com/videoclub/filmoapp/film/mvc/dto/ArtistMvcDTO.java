package com.videoclub.filmoapp.film.mvc.dto;


import com.videoclub.filmoapp.core.validator.EnumValid;
import com.videoclub.filmoapp.film.domain.ArtistType;
import com.videoclub.filmoapp.film.dto.ArtistDTO;
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
    @EnumValid(target = ArtistDTO.ArtistType.class, required = true , message = "{validation.film.status.invalid}")
    private ArtistType artistType;


}
