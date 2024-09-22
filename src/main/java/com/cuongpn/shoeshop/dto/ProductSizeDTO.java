package com.cuongpn.shoeshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSizeDTO {
    private Long product_id;
    private Long size_id;
    private Long value;
    private Integer stock;

}
