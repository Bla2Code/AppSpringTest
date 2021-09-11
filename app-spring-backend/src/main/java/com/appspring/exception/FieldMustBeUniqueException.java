package com.appspring.exception;

public class FieldMustBeUniqueException extends RuntimeException {
    public FieldMustBeUniqueException(String message) {
        super(message);
    }
}
