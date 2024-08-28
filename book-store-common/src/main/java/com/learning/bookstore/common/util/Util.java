package com.learning.bookstore.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.bookstore.common.constants.Constants;
import com.learning.bookstore.common.constants.ErrorCodes;
import com.learning.bookstore.common.exception.ParsingException;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Util {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Random random = new Random();

    public static <T> T readString(String string, Class<T> aClass) throws ParsingException {

        T object;
        try {
            object = objectMapper.readValue(string, aClass);
        } catch (JsonProcessingException e) {
            throw new ParsingException("Error in parsing string", ErrorCodes.PARSING_ERROR);
        }

        return object;
    }

    public static <T> String convertToString(T object) throws ParsingException {

        String value;
        try {
            value = objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new ParsingException("Error in parsing string", ErrorCodes.PARSING_ERROR);
        }

        return value;
    }

    public static String readRequest(InputStream inputStream) throws IOException {

        var reader = new InputStreamReader(inputStream);
        var buffer = new BufferedReader(reader);
        var requestString = new StringBuilder();

        String line;
        while ((line = buffer.readLine()) != null) {
            requestString.append(line);
        }
        return requestString.toString();
    }

    public static Map<String, String> parseQueryParams(String query) {
        if (query == null || query.isEmpty()) {
            return Map.of();
        }
        return Stream.of(query.split("&"))
                .map(param -> param.split("="))
                .collect(Collectors.toMap(arr -> arr[0], arr -> arr.length > 1 ? arr[1] : ""));
    }

    public static <E, D> E map(ModelMapper mapper, D object, Class<E> entityClass) {

        return mapper.map(object, entityClass);
    }

    public static String generateTransaction() {

        long currentTimeMillis = Instant.now().toEpochMilli();
        int randomNumber = random.nextInt(10000);
        return String.format("%s-%d-%04d", "TXN", currentTimeMillis, randomNumber);
    }

    public static <T> ResponseEntity<T> createResponse(T body, HttpStatus httpStatus, long startTime, HttpHeaders headers) {

        if (headers == null)
            headers = new HttpHeaders();

        headers.put(Constants.TRANSACTION_ID, List.of(Thread.currentThread().getName()));
        headers.put(Constants.EXECUTION_TIME, List.of(String.valueOf(System.currentTimeMillis() - startTime)));


        return new ResponseEntity<>(body, headers, httpStatus);
    }

    public static <T> ResponseEntity<T> createResponse(T body, HttpStatus httpStatus, long startTime) {

        var headers = new HttpHeaders();

        headers.put(Constants.TRANSACTION_ID, List.of(Thread.currentThread().getName()));
        headers.put(Constants.EXECUTION_TIME, List.of(String.valueOf(System.currentTimeMillis() - startTime)));

        return new ResponseEntity<>(body, headers, httpStatus);
    }

    public static ResponseEntity createResponse(HttpStatus httpStatus, long startTime) {

        var headers = new HttpHeaders();

        headers.put(Constants.TRANSACTION_ID, List.of(Thread.currentThread().getName()));
        headers.put(Constants.EXECUTION_TIME, List.of(String.valueOf(System.currentTimeMillis() - startTime)));

        return new ResponseEntity<>(headers, httpStatus);
    }

    public static <T> ResponseEntity<T> createResponse(T body, HttpStatus httpStatus) {

        return new ResponseEntity<>(body, httpStatus);
    }

    public static String getTransactionId(HttpServletRequest httpRequest) {
        var transactionId = httpRequest.getHeader(Constants.TRANSACTION_ID) != null ?
                httpRequest.getHeader(Constants.TRANSACTION_ID) : generateTransaction();
        return transactionId;
    }

    public static String getTransactionId(RequestEntity requestEntity) {
        var transactionId = requestEntity.getHeaders().get(Constants.TRANSACTION_ID) != null ?
                requestEntity.getHeaders().getFirst(Constants.TRANSACTION_ID) : generateTransaction();
        return transactionId;
    }


}
