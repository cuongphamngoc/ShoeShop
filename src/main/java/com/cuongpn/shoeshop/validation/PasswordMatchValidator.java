package com.cuongpn.shoeshop.validation;

import com.cuongpn.shoeshop.dto.ChangePasswordForm;
import com.cuongpn.shoeshop.service.UserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, Object> {

    @Autowired
    private UserService userService;

    @Override
    public void initialize(PasswordMatch constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object passwordForm, ConstraintValidatorContext context) {

        if( passwordForm instanceof ChangePasswordForm){
            return Objects.equals(((ChangePasswordForm) passwordForm).getNewPassword(), ((ChangePasswordForm) passwordForm).getConfirmPassword());
        }
        else return false;
    }
}