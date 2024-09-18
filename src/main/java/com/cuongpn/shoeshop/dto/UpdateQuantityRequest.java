package com.cuongpn.shoeshop.dto;

import lombok.Data;

@Data
public class UpdateQuantityRequest {
    private Long cartItemId;
    private Integer stock;
}
