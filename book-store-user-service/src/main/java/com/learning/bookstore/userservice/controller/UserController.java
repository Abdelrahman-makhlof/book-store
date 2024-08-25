package com.learning.bookstore.userservice.controller;

import com.learning.bookstore.common.constants.ErrorCodes;
import com.learning.bookstore.common.exception.ApplicationException;
import com.learning.bookstore.common.exception.ValidationException;
import com.learning.bookstore.common.interfaces.ValidEmail;
import com.learning.bookstore.common.interfaces.ValidUsername;
import com.learning.bookstore.common.util.Util;
import com.learning.bookstore.userservice.dto.UserDTO;
import com.learning.bookstore.userservice.request.UserRequest;
import com.learning.bookstore.userservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDTO> addUser(@RequestBody @Valid UserDTO userDTO, HttpServletRequest httpServletRequest) throws ValidationException {
        var transactionId = httpServletRequest.getHeader("transaction-id");
        Thread.currentThread().setName(transactionId != null ? transactionId : Util.generateTransaction());

        var user = userService.addUser(userDTO);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<UserDTO> getUser(@RequestParam(required = false) @ValidUsername String username, @RequestParam(required = false) @ValidEmail String email,
                                           HttpServletRequest httpServletRequest) throws ApplicationException, ValidationException {
        var transactionId = httpServletRequest.getHeader("transaction-id");
        Thread.currentThread().setName(transactionId != null ? transactionId : Util.generateTransaction());
        if (username == null && email == null)
            throw new ValidationException("Username or email is mandatory", ErrorCodes.MANDATORY_PARAMETER_IS_MISSING);

        var user = userService.getUserByUsernameOrEmail(username, email);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<UserDTO> updateUser(@RequestParam @ValidUsername String username, @RequestBody @Valid UserRequest userRequest,
                                              HttpServletRequest httpServletRequest) throws ApplicationException {

        var transactionId = httpServletRequest.getHeader("transaction-id");
        Thread.currentThread().setName(transactionId != null ? transactionId : Util.generateTransaction());

        var user = userService.updateUser(username, userRequest);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity deleteUser(@RequestParam(required = false) @ValidUsername String username, @RequestParam(required = false) @ValidEmail String email,
                                     HttpServletRequest httpServletRequest) throws ValidationException {
        var transactionId = httpServletRequest.getHeader("transaction-id");
        Thread.currentThread().setName(transactionId != null ? transactionId : Util.generateTransaction());
        if (username == null && email == null)
            throw new ValidationException("Username or email is mandatory", ErrorCodes.MANDATORY_PARAMETER_IS_MISSING);
        userService.deleteUser(username, email);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
