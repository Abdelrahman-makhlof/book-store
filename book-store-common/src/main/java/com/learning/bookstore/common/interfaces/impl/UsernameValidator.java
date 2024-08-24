package com.learning.bookstore.common.interfaces.impl;

import com.learning.bookstore.common.constants.Constants;
import com.learning.bookstore.common.interfaces.ValidPassword;
import com.learning.bookstore.common.interfaces.ValidUsername;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UsernameValidator implements ConstraintValidator<ValidUsername, String> {
    @Override
    public void initialize(ValidUsername constraintAnnotation) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        return password != null && password.matches(Constants.USERNAME_REGEX);

    }
}
