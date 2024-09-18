package com.cuongpn.shoeshop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "product_size")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductSize {
    @EmbeddedId
    private ProductSizeId id;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @MapsId("sizeId")
    @JoinColumn(name = "size_id")
    private Size size;

    @Column(nullable = false)
    private int stock;

    @OneToMany(mappedBy = "productSize",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;
}
