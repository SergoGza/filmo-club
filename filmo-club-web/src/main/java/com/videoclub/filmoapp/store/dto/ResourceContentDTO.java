package com.videoclub.filmoapp.store.dto;

import java.util.UUID;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class ResourceContentDTO {

    UUID resourceId;
    byte[] content;
    String contentType;
    String filename;
    String description;
    int size;

}
