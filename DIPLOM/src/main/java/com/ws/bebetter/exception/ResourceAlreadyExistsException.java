package com.ws.bebetter.exception;

/**
 * Исключение, выбрасываемое при попытке создать объект, который уже существует.
 */
public class ResourceAlreadyExistsException extends RuntimeException {

    public ResourceAlreadyExistsException(String message) {
        super(message);
    }

}
