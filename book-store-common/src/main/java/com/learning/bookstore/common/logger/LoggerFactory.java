package com.learning.bookstore.common.logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.learning.bookstore.common.constants.LogKeys;
import org.apache.logging.log4j.message.ObjectMessage;

public class LoggerFactory {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Log log(String message) {
        return new Log(LogKeys.MESSAGE, message);
    }


    public static class Log {
        private ObjectNode jsonData;

        public Log(String key, String value) {
            jsonData = objectMapper.createObjectNode();
            jsonData.put(key, value);
        }

        public Log put(String key, String value) {
            jsonData.put(key, value);
            return this;
        }

        public ObjectMessage build() {
            jsonData.put(LogKeys.TRANSACTION_ID, Thread.currentThread().getName());
            return new ObjectMessage(this.jsonData);
        }
    }

}
