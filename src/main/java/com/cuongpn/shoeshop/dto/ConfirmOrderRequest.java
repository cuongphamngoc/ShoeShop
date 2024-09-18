package com.cuongpn.shoeshop.dto;

import com.cuongpn.shoeshop.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConfirmOrderRequest {
    private List<CartItemDTO> cartItems;
    private Long addressId;
    private PaymentMethod paymentMethod;
    @NotBlank
    private String fullName;
    @Pattern(regexp = "^(0?)(3[2-9]|5[6|8|9]|7[0|6-9]|8[0-6|8|9]|9[0-4|6-9])[0-9]{7}$", message = "Số điện thoại không hợp lệ")
    private String phoneNumber;

}
