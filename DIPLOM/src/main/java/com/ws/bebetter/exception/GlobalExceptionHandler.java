package com.ws.bebetter.exception;

import com.ws.bebetter.config.FileUploadConfig;
import com.ws.bebetter.web.dto.ErrorRs;
import io.minio.errors.MinioException;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Collections;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final FileUploadConfig fileUploadConfig;

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorRs> handleBadRequestException(BadRequestException e) {
        log.error(e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorRs.builder()
                        .errors(Collections.singletonList(e.getMessage()))
                        .build());
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorRs> handleResourceAlreadyExistsException(ResourceAlreadyExistsException e) {
        log.error(e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorRs.builder()
                        .errors(Collections.singletonList(e.getMessage()))
                        .build());
    }

    @ExceptionHandler(OperationNotAllowedByCurrentUserRole.class)
    public ResponseEntity<ErrorRs> handleOperationNotAllowedByCurrentUser(OperationNotAllowedByCurrentUserRole e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorRs.builder()
                        .errors(Collections.singletonList(e.getMessage()))
                        .build());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorRs> handleAccessDeniedException(AccessDeniedException e) {
        log.error(e.getMessage());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ErrorRs.builder()
                        .errors(Collections.singletonList("Доступ запрещен: " + e.getMessage()))
                        .build());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorRs> handleMaxUploadSizeExceededException() {
        log.error("The maximum allowable size of the uploaded file has been exceeded: {}",
                fileUploadConfig.getMaxFileSize());

        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(ErrorRs.builder()
                        .errors(Collections.singletonList("Превышен максимально допустимый размер загружаемого файла"
                                 + fileUploadConfig.getMaxFileSize()))
                        .build());
    }

    @ExceptionHandler(MinioException.class)
    public ResponseEntity<ErrorRs> handleMinioException(MinioException e) {
        log.error("Error uploading file to MinIO: {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorRs.builder()
                        .errors(Collections.singletonList("Ошибка при загрузке файла в файловый сервис: "
                                + e.getMessage()))
                        .build());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorRs> handleValidationRoleExceptions(HttpMessageNotReadableException e) {
        log.error("Invalid role value: {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorRs.builder()
                        .errors(Collections.singletonList(e.getMessage()))
                        .build());
    }

    @ExceptionHandler(RoleNotAllowedException.class)
    public ResponseEntity<ErrorRs> handleNotAllowedRoleExceptions(RoleNotAllowedException e) {
        log.error("Not allowed set role: {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorRs.builder()
                        .errors(Collections.singletonList(e.getMessage()))
                        .build());
    }

    @ExceptionHandler(OperationNotAllowedByCurrentCompany.class)
    public ResponseEntity<ErrorRs> handleOperationNotAllowedByCurrentCompany(OperationNotAllowedByCurrentCompany e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorRs.builder()
                        .errors(Collections.singletonList(e.getMessage()))
                        .build());
    }

    @ExceptionHandler(PhotoSetupException.class)
    public ResponseEntity<ErrorRs> handlePhotoSetupException(PhotoSetupException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorRs.builder()
                        .errors(Collections.singletonList(e.getMessage()))
                        .build());
    }

}
