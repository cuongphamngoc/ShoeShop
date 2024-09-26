package com.cuongpn.shoeshop.validation;

import com.cuongpn.shoeshop.dto.ChangePasswordForm;
import com.cuongpn.shoeshop.entity.User;
import com.cuongpn.shoeshop.service.UserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidOldPasswordValidator implements ConstraintValidator<ValidOldPassword, Object> {


    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void initialize(ValidOldPassword constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object passwordForm, ConstraintValidatorContext context) {

        if( !(passwordForm instanceof ChangePasswordForm form)) return false;
        String userName = form.getUserName();
        User user = userService.findByUserName(userName);
        System.out.println(user);
        if(passwordEncoder.matches(form.getOldPassword(), user.getPassword())) return true;
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode("oldPassword")
                .addConstraintViolation();
        return false;

    }
}