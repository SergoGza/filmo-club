package com.videoclub.filmoapp.core.port;

import com.videoclub.filmoapp.core.dto.ResourceContentDTO;
import com.videoclub.filmoapp.core.dto.ResourceIdDTO;
import jakarta.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface StoreFacade {

    Optional<ResourceIdDTO> saveResource(MultipartFile multipartFile, @Nullable String description);

    Optional<ResourceContentDTO> findResource(UUID resourceid);

    void deleteResource(UUID resourceID);

}