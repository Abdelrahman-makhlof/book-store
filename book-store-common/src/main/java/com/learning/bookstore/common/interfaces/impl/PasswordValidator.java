package com.learning.bookstore.common.interfaces.impl;

import com.learning.bookstore.common.constants.Constants;
import com.learning.bookstore.common.interfaces.ValidPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {
    @Override
    public void initialize(ValidPassword constraintAnnotation) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        return password != null && password.matches(Constants.PASSWORD_REGEX);

    }
}
