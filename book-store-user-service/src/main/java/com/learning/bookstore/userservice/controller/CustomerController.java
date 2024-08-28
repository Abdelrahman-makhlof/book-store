package com.learning.bookstore.userservice.controller;

import com.learning.bookstore.common.constants.ErrorCodes;
import com.learning.bookstore.common.dto.CustomerDTO;
import com.learning.bookstore.common.exception.ApplicationException;
import com.learning.bookstore.common.exception.ValidationException;
import com.learning.bookstore.common.interfaces.ValidEmail;
import com.learning.bookstore.common.interfaces.ValidUsername;
import com.learning.bookstore.common.util.Util;
import com.learning.bookstore.userservice.request.UserRequest;
import com.learning.bookstore.userservice.service.CustomerService;
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
@RequestMapping("api/v1/customers")
public class CustomerController {

    private final CustomerService userService;

    @PostMapping
    public ResponseEntity<CustomerDTO> addUser(@RequestBody @Valid CustomerDTO customerDTO, HttpServletRequest httpServletRequest) throws ValidationException {
        long startTime = System.currentTimeMillis();
        var transactionId = httpServletRequest.getHeader("transaction-id");
        Thread.currentThread().setName(transactionId != null ? transactionId : Util.generateTransaction());

        var customer = userService.addCustomer(customerDTO);
        return Util.createResponse(customer, HttpStatus.CREATED, startTime);
    }

    @GetMapping
    public ResponseEntity<CustomerDTO> getUser(@RequestParam(required = false) @ValidUsername String username, @RequestParam(required = false) @ValidEmail String email,
                                               HttpServletRequest httpServletRequest) throws ApplicationException, ValidationException {
        long startTime = System.currentTimeMillis();
        var transactionId = httpServletRequest.getHeader("transaction-id");
        Thread.currentThread().setName(transactionId != null ? transactionId : Util.generateTransaction());
        if (username == null && email == null)
            throw new ValidationException("Username or email is mandatory", ErrorCodes.MANDATORY_PARAMETER_IS_MISSING);

        var customer = userService.getCustomerByUsernameOrEmail(username, email);

        return Util.createResponse(customer, HttpStatus.OK, startTime);
    }

    @PutMapping
    public ResponseEntity<CustomerDTO> updateUser(@RequestParam @ValidUsername String username, @RequestBody @Valid UserRequest userRequest,
                                                  HttpServletRequest httpServletRequest) throws ApplicationException {

        long startTime = System.currentTimeMillis();
        var transactionId = httpServletRequest.getHeader("transaction-id");
        Thread.currentThread().setName(transactionId != null ? transactionId : Util.generateTransaction());

        var customer = userService.updateCustomer(username, userRequest);
        return Util.createResponse(customer, HttpStatus.OK, startTime);
    }

    @DeleteMapping
    public ResponseEntity deleteUser(@RequestParam(required = false) @ValidUsername String username, @RequestParam(required = false) @ValidEmail String email,
                                     HttpServletRequest httpServletRequest) throws ValidationException {
        long startTime = System.currentTimeMillis();
        var transactionId = httpServletRequest.getHeader("transaction-id");
        Thread.currentThread().setName(transactionId != null ? transactionId : Util.generateTransaction());
        if (username == null && email == null)
            throw new ValidationException("Username or email is mandatory", ErrorCodes.MANDATORY_PARAMETER_IS_MISSING);
        userService.deleteCustomer(username, email);

        return Util.createResponse(HttpStatus.OK, startTime);
    }

}
