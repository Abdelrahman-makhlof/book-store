package com.learning.bookstore.common.exception;

import lombok.Data;

@Data
public class DatabaseException extends Exception {

    private String errorCode;

    public DatabaseException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
