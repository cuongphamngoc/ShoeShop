package com.cuongpn.shoeshop.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Shipping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long id;
    private String name;
    private String phoneNumber;
    private LocalDateTime shippingDate;
    @ManyToOne
    @JoinColumn(name = "address_id")

    private Address address;
    @OneToOne
    @JoinColumn(name="order_id")
    private Order order;
}
