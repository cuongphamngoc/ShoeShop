package com.cuongpn.shoeshop.dto;

import com.cuongpn.shoeshop.validation.PasswordMatch;
import com.cuongpn.shoeshop.validation.ValidOldPassword;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@PasswordMatch
@ValidOldPassword
public class ChangePasswordForm {

    @NotNull
    private String userName;
    @NotBlank
    private String oldPassword;
    @NotBlank
    private String newPassword;
    @NotBlank
    private String confirmPassword;
}
