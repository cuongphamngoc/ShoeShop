package com.cuongpn.shoeshop.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
public class Shipping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String phoneNumber;
    @OneToOne
    @JoinColumn(name = "address_id")

    private Address address;
    @OneToOne
    private Order order;
}
