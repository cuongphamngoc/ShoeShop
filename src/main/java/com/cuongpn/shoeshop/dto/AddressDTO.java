package com.cuongpn.shoeshop.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {
    @NotNull
    private Long id  = 0L ;
    @NotBlank
    private String name;
    @NotBlank
    private String streetAddress;
    @NotBlank
    private String city;
    @NotBlank
    private String district;
    @Pattern(regexp = "^\\d{6}$")
    private String zipCode;
}
