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
    public boolean isValid(Object object, ConstraintValidatorContext context) {

        if (!(object instanceof ChangePasswordForm form)) return false;
        if(Objects.equals(form.getNewPassword(), form.getConfirmPassword())) return  true;
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode("confirmPassword")
                .addConstraintViolation();
        return false;


    }
}