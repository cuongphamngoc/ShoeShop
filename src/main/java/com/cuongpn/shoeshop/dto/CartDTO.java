package com.cuongpn.shoeshop.dto;

import com.cuongpn.shoeshop.entity.Cart;
import jakarta.persistence.Entity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data

public class CartDTO {
    List<CartItemDTO> cartItems;

    private BigDecimal totalPrice;

}
