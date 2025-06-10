package com.ws.bebetter.web.controller;

import com.ws.bebetter.service.S3StorageService;
import com.ws.bebetter.web.dto.RefDto;
import com.ws.bebetter.web.dto.StorageRefDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/files/")
@RequiredArgsConstructor
public class FileController {

    private final S3StorageService s3StorageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public RefDto uploadFile(@RequestPart(value = "file") MultipartFile file) {
        return s3StorageService.uploadFile(file);
    }

    @GetMapping(value = "{path}/", produces = MediaType.APPLICATION_JSON_VALUE)
    public StorageRefDto getFile(@PathVariable UUID path) {
        return s3StorageService.getFile(path);
    }

}