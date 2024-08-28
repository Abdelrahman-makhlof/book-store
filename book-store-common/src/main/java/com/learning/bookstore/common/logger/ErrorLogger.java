package com.learning.bookstore.common.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ObjectMessage;
import org.springframework.stereotype.Component;

@Component
public class ErrorLogger {

    private static Logger errorLogger = null;

    ErrorLogger() {
        errorLogger = LogManager.getLogger("errorLogger");
    }

    public static void error(String message) {
        errorLogger.error(LoggerFactory.log(message).build());
    }

    public static void error(ObjectMessage message) {
        errorLogger.error(message);
    }

    public static void error(String message, Throwable throwable) {
        errorLogger.error(LoggerFactory.log(message).build(), throwable);
    }

    public static void error(ObjectMessage message, Throwable throwable) {
        errorLogger.error(message, throwable);
    }

}
