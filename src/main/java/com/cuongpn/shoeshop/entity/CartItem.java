package com.cuongpn.shoeshop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer qty;
    private BigDecimal price;

    private BigDecimal totalPrice;
    @OneToOne
    @JoinColumns({
            @JoinColumn(name="product_id",referencedColumnName = "product_id"),
            @JoinColumn(name="size_id", referencedColumnName="size_id")
    })

    private ProductSize productSize;



    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    public void setTotalPrice(){
        totalPrice= price.multiply(BigDecimal.valueOf(qty));
    }

}
