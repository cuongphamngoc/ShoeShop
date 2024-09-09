package com.cuongpn.shoeshop.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignInForm {
    @NotBlank
    private  String username;
    @NotBlank
    private String password;
}
