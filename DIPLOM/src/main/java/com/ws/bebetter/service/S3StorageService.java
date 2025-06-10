package com.ws.bebetter.service;

import com.ws.bebetter.web.dto.RefDto;
import com.ws.bebetter.web.dto.StorageRefDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Сервис по управлению файлами
 */
public interface S3StorageService {

    /**
     * Загружает новый файл и сохраняет его в базу данных.
     *
     * @param file загружаемый файл.
     * @return {@link RefDto}, содержащий информацию о загруженном файле.
     */
    RefDto uploadFile(MultipartFile file);

    /**
     * Получает файл по уникальному глобальному строковому идентификатору.
     *
     * @param guid уникальный глобальный строковый идентификатор.
     * @return {@link StorageRefDto}, содержащий ссылку на файл в s3 хранилище.
     */
    StorageRefDto getFile(UUID guid);
}
