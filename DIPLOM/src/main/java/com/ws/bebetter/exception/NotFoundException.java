package com.ws.bebetter.exception;

/**
 * Исключение, выбрасываемое при попытке получить несуществующий ресурс.
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }

}
