package com.cuongpn.shoeshop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name="product_id", referencedColumnName="product_id"),
            @JoinColumn(name="size_id", referencedColumnName="size_id")
    })
    private ProductSize productSize;

    private Integer qty;

    private BigDecimal price;

    private BigDecimal totalPrice;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    public BigDecimal getSubTotal(){
        return price.multiply(BigDecimal.valueOf(qty));
    }

}
