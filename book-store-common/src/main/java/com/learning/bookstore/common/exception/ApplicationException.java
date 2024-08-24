package com.learning.bookstore.common.exception;

import lombok.Data;

@Data
public class ApplicationException extends Exception {

    private String errorCode;

    public ApplicationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
