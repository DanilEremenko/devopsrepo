package com.ws.bebetter.service.impl;

import com.ws.bebetter.config.S3ServiceConfig;
import com.ws.bebetter.entity.FileRef;
import com.ws.bebetter.entity.FileSpecification;
import com.ws.bebetter.exception.NotFoundException;
import com.ws.bebetter.repository.FileRefRepository;
import com.ws.bebetter.service.S3StorageService;
import com.ws.bebetter.web.dto.RefDto;
import com.ws.bebetter.web.dto.StorageRefDto;
import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class S3StorageServiceImpl implements S3StorageService {

    private final FileRefRepository fileRefRepository;
    private final MinioClient minioClient;
    private final S3ServiceConfig s3ServiceConfig;

    @Override
    public RefDto uploadFile(MultipartFile file) {
        UUID objectName = UUID.randomUUID();

        Map<String, String> fileMetaData = new HashMap<>();
        if (file.getOriginalFilename() != null) {
            fileMetaData.put("file-name", file.getOriginalFilename());
        }

        try {

            boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(s3ServiceConfig.getMinioBucketName())
                    .build());
            if (!isExist) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(s3ServiceConfig.getMinioBucketName())
                        .build());
            }

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(s3ServiceConfig.getMinioBucketName())
                    .object(objectName.toString())
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .userMetadata(fileMetaData)
                    .build());

        } catch (MinioException e) {
            throw new RuntimeException("Ошибка при загрузке файла в файловый сервис: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        FileSpecification specification = getFileSpecification(file);

        FileRef fileRef = FileRef.builder()
                .id(objectName)
                .mimeType(file.getContentType())
                .fileName(file.getOriginalFilename())
                .fileSpecification(specification)
                .build();

        fileRefRepository.save(fileRef);
        log.info("Upload file {} with GUID {}", file.getOriginalFilename(), objectName);

        return RefDto.builder()
                .guid(objectName)
                .mimeType(file.getContentType())
                .fileName(file.getOriginalFilename())
                .fileSpecification(specification)
                .build();
    }

    @Override
    public StorageRefDto getFile(UUID guid) {
        FileRef fileRef = fileRefRepository.findById(guid)
                .orElseThrow(() -> new NotFoundException("Файл не найден: " + guid));

        try {
            String urlReference = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(s3ServiceConfig.getMinioBucketName())
                            .object(fileRef.getId().toString())
                            .expiry(7, TimeUnit.DAYS)
                            .build());

            return StorageRefDto.builder()
                    .reference(urlReference)
                    .build();
        } catch (MinioException e) {
            throw new RuntimeException("Ошибка при получении файла в файловом сервисе: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private FileSpecification getFileSpecification(MultipartFile file) {
        return Objects.requireNonNull(file.getContentType())
                .contains("image") ? FileSpecification.IMAGE : FileSpecification.DOCUMENT;
    }
}
