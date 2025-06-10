package com.ws.bebetter.exception;

public class OperationNotAllowedByCurrentCompany extends RuntimeException {
    public OperationNotAllowedByCurrentCompany(String s) {
        super(s);
    }
}