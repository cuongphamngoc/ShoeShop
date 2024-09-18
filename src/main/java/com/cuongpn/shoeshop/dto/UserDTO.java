package com.cuongpn.shoeshop.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String avatar;
    private String email;
}
