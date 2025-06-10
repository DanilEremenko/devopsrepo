package com.ws.bebetter.service;

import com.ws.bebetter.entity.FileRef;
import com.ws.bebetter.entity.UserProfile;
import com.ws.bebetter.web.dto.RefDto;

import java.util.UUID;

/**
 * Сервис для работы с фото
 */
public interface FileRefService {

    /**
     * Получение фото по глобшальному идентификатору
     *
     * @param photoGuid идентификатор
     * @return {@link FileRef}
     */
    FileRef getPhoto(UUID photoGuid);

    /**
     * Удаление фото по глобальному идентификатору
     *
     * @param photoGuid идентификатор
     */
    void deletePhoto(UUID photoGuid);

    /**
     * Установка фотографии профиля с проверкой специфики файла(изображение/документ)
     *
     * @param refDto  данные фото
     * @param profile профиль пользователя
     */
    void setPhotoToProfile(RefDto refDto, UserProfile profile);
}
