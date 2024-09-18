package com.cuongpn.shoeshop.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {
    private Long id;
    private String productName;
    private Long size;
    private String productImage;
    private Integer qty;
    private BigDecimal price;
    private BigDecimal totalPrice;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItemDTO that = (CartItemDTO) o;
        return  Objects.equals(size,that.size)&&
                Objects.equals(qty,that.qty)&&
                Objects.equals(id, that.id) &&
                Objects.equals(productName, that.productName) &&
                Objects.equals(productImage,that.productImage)&&
                (price == null ? that.price == null : price.compareTo(that.price) == 0) &&
                (totalPrice == null ? that.totalPrice == null : totalPrice.compareTo(that.totalPrice) == 0);

    }

    @Override
    public int hashCode() {
        return Objects.hash(id, productName, size, qty, price, totalPrice);
    }

    @Override
    public String toString(){
        return "{ id " + id +", productName "+  productName +", productImage  "+ productImage+", size + " + size +", price "+ price +", qty "+ qty +", totalPrice "+ totalPrice + "}";
    }
}
