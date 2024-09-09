package com.cuongpn.shoeshop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name="`order`")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime orderDate;
    private LocalDateTime  shippingDate;
    private String orderStatus;
    private BigDecimal orderTotal;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
}
