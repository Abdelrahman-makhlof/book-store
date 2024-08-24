package com.learning.bookstore.common.exception;

import lombok.Data;

@Data
public class ParsingException extends Exception {

    private String errorCode;

    public ParsingException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
