package com.ws.bebetter.exception;

/**
 * Исключение, выбрасываемое при попытке обратиться к ресурсу, который не доступен для данной роли.
 */
public class RoleNotAllowedException extends RuntimeException {

    public RoleNotAllowedException(String message) {
        super(message);
    }
}