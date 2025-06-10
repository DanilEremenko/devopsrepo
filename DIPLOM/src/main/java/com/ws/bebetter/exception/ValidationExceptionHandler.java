package com.ws.bebetter.exception;

import com.ws.bebetter.web.dto.ErrorRs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ValidationExceptionHandler {

    public static final String TYPE_MISMATCH = "typeMismatch";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorRs> handleValidationExceptions(MethodArgumentNotValidException e) {
        log.info("Validation error: {}", e.getMessage());

        List<String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> {
                    if (Arrays.asList(Objects.requireNonNull(error.getCodes())).contains(TYPE_MISMATCH)) {
                        Class<?> exceptionParameterType = e.getParameter().getParameterType();
                        String enumValues;

                        try {
                            Field field = exceptionParameterType.getDeclaredField(error.getField());
                            Class<?> enumType = field.getType();
                            Object[] enumConstants = enumType.getEnumConstants();
                            enumValues = Arrays.toString(enumConstants);
                        } catch (NoSuchFieldException ex) {
                            throw new RuntimeException(ex);
                        }

                        return "В поле '%s' указано некорректное значение '%s'. Допустимыми значениями являются: %s"
                                .formatted(error.getField(), error.getRejectedValue(), enumValues.toLowerCase());
                    }
                    return String.format("Некорректное значение в поле '%s': %s",
                            error.getField(), error.getDefaultMessage());
                }).toList();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorRs.builder()
                        .errors(errors)
                        .build());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorRs> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Class<?> requiredType = ex.getRequiredType();
        String errorMessage;

        if (requiredType != null && requiredType.isEnum()) {
            errorMessage = "Некорректное значение параметра '" + ex.getName() + "'. "
                    + "Корректными значениями являются: " + Arrays.toString(requiredType.getEnumConstants());
        } else {
            errorMessage = "Некорректное значение параметра: " + ex.getName();
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorRs.builder()
                        .errors(Collections.singletonList(errorMessage))
                        .build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorRs> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorRs.builder()
                        .errors(Collections.singletonList(e.getMessage()))
                        .build());
    }

}
