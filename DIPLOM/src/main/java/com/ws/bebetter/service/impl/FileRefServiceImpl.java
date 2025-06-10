package com.ws.bebetter.service.impl;

import com.ws.bebetter.entity.FileRef;
import com.ws.bebetter.entity.UserProfile;
import com.ws.bebetter.exception.NotFoundException;
import com.ws.bebetter.exception.PhotoSetupException;
import com.ws.bebetter.repository.FileRefRepository;
import com.ws.bebetter.service.FileRefService;
import com.ws.bebetter.web.dto.RefDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileRefServiceImpl implements FileRefService {
    private final FileRefRepository fileRefRepository;

    @Override
    public FileRef getPhoto(UUID photoGuid) {
        return fileRefRepository.findById(photoGuid)
                .orElseThrow(() -> new NotFoundException("Файл с идентификатором %s не найден"
                        .formatted(photoGuid.toString())));
    }

    @Override
    public void deletePhoto(UUID photoGuid) {
        FileRef deletedPhoto = fileRefRepository.findById(photoGuid)
                .orElseThrow(() -> new NotFoundException("Файл с идентификатором %s не найден"
                        .formatted(photoGuid.toString())));
        fileRefRepository.delete(deletedPhoto);
    }

    @Override
    public void setPhotoToProfile(RefDto refDto, UserProfile profile) {
        if (refDto == null) {
            return;
        }
        FileRef newPhoto = getPhoto(refDto.getGuid());

        if (!refDto.getFileSpecification().equals(newPhoto.getFileSpecification())) {
            throw new PhotoSetupException(("Пожалуйста, укажите корректный тип файла в вашем запросе: " +
                    "указан: %s, должен быть: %s")
                    .formatted(refDto.getFileSpecification(), newPhoto.getFileSpecification()));
        }
        switch (newPhoto.getFileSpecification()) {
            case IMAGE -> profile.setPhoto(newPhoto);
            case DOCUMENT -> throw new PhotoSetupException("Невозможно установить документ в качестве фото профиля");
        }
    }
}
