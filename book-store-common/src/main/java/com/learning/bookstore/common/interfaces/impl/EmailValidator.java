package com.learning.bookstore.common.interfaces.impl;

import com.learning.bookstore.common.constants.Constants;
import com.learning.bookstore.common.interfaces.ValidEmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmailValidator implements ConstraintValidator<ValidEmail, String> {
    @Override
    public void initialize(ValidEmail constraintAnnotation) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        return password != null && password.matches(Constants.EMAIL_REGEX);

    }
}
