package com.learning.bookstore.common.exception;

import lombok.Data;

@Data
public class ValidationException extends Exception {

    private String errorCode;

    public ValidationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
