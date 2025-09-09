package com.videoclub.filmoapp.film.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class FilmImageResourceDTO {

    String contentType;
    int size;
    String filename;
    byte[] content;

}
