package com.cuongpn.shoeshop.dto;

import lombok.Data;

@Data
public class AddCartRequest {
    private Long product_id;
    private Long size_id;
    private Integer qty;


}
