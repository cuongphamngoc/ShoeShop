package com.cuongpn.shoeshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
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
