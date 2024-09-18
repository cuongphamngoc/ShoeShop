package com.cuongpn.shoeshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private BigDecimal totalPrice;
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL,mappedBy = "cart")
    List<CartItem> cartItems = new ArrayList<>();

    private void setTotalPrice(){
        BigDecimal totalPrice = new BigDecimal(0);
        for(CartItem cart: cartItems){
            totalPrice = totalPrice.add(cart.getPrice().multiply(BigDecimal.valueOf(cart.getQty())));
        }
    }

}
