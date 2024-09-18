package com.cuongpn.shoeshop.entity;

import com.cuongpn.shoeshop.enums.OrderStatus;
import com.cuongpn.shoeshop.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name="`order`")
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime orderDate;
    private LocalDateTime  shippingDate;
    private PaymentMethod paymentMethod;
    private OrderStatus orderStatus;
    private BigDecimal orderTotal;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(orphanRemoval = true,cascade = CascadeType.ALL,mappedBy = "order")
    private List<OrderItem> orderItems;

    private void setOrderTotal(){
        for(OrderItem orderItem: orderItems){
            orderTotal = orderTotal.add(orderItem.getSubTotal());
        }
    }
}
