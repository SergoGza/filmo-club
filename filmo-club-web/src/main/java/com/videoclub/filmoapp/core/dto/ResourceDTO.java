package com.videoclub.filmoapp.core.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Value
@Builder
@Jacksonized
public class ResourceDTO {

    private UUID resourceId;
    private String filename;
    private String contentType;
    private int size;

}
