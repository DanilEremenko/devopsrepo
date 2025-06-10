package com.ws.bebetter.web.mapper;

import com.ws.bebetter.entity.FileRef;
import com.ws.bebetter.web.dto.RefDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileRefMapper extends Mappable<FileRef, RefDto> {
    @Override
    default RefDto toDto(FileRef entity) {
        if (entity == null) {
            return null;
        }

        return RefDto.builder()
                .guid(entity.getId())
                .fileName(entity.getFileName())
                .mimeType(entity.getMimeType())
                .fileSpecification(entity.getFileSpecification())
                .build();
    }
}