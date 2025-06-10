package com.ws.bebetter.exception;

public class OperationNotAllowedByCurrentUserRole extends RuntimeException {
    public OperationNotAllowedByCurrentUserRole(String message) {
        super(message);
    }
}