package com.appspring.security;

import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    private static final Pattern allowedCharactersPattern = Pattern.compile("^[a-zA-Z0-9!@#$%^&*]{1,8}$");

    @Override
    public void initialize(ValidPassword arg0) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (!StringUtils.isEmpty(password)) {
            return allowedCharactersPattern.matcher(password).matches();
        }
        return true;
    }
}