package com.learning.bookstore.common.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ObjectMessage;
import org.springframework.stereotype.Component;

@Component
public class ApplicationLogger {

    private static Logger logger = null;

    ApplicationLogger() {
        logger = LogManager.getLogger("applicationLogger");

    }

    public static void info(String message) {
        logger.info(LoggerFactory.log(message).build());
    }

    public static void debug(String message) {
        logger.debug(LoggerFactory.log(message).build());
    }

    public static void info(ObjectMessage message) {
        logger.info(message);
    }

    public static void debug(ObjectMessage message) {
        logger.debug(message);
    }


}
