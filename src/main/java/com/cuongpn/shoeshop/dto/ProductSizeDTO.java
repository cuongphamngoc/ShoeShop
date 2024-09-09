package com.cuongpn.shoeshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductSizeDTO {
    private Long product_id;
    private Long size_id;
    private Long value;
    private Integer stock;

}
