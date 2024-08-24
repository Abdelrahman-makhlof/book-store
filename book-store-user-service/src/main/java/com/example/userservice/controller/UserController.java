package com.example.userservice.controller;

import com.example.userservice.service.UserService;
import com.learning.bookstore.common.constants.ErrorCodes;
import com.learning.bookstore.common.dto.UserDTO;
import com.learning.bookstore.common.exception.ValidationException;
import com.learning.bookstore.common.util.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDTO> addUser(RequestEntity<UserDTO> requestEntity) throws ValidationException {
        var transactionId = requestEntity.getHeaders().get("transaction-id");
        Thread.currentThread().setName(transactionId != null ? transactionId.get(0) : Util.generateTransaction());
        if (requestEntity.getBody() == null || requestEntity.getBody().getUsername() == null ||
                requestEntity.getBody().getFullName() == null || requestEntity.getBody().getEmail() == null ||
                requestEntity.getBody().getPassword() == null)
            throw new ValidationException("Mandatory parameter is missing", ErrorCodes.MANDATORY_PARAMETER_IS_MISSING);

        if (Util.isValidPassword(requestEntity.getBody().getPassword()))
            throw new ValidationException("Invalid password format", ErrorCodes.INVALID_FORMAT);

        var user = userService.addUser(requestEntity.getBody());
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }
}
