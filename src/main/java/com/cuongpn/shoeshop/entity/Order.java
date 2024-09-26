package com.cuongpn.shoeshop.entity;

import com.cuongpn.shoeshop.enums.OrderStatus;
import com.cuongpn.shoeshop.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name="`order`")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@NamedEntityGraph(
        name = "order.user",
        attributeNodes = {
                @NamedAttributeNode("user")
        }
)
@NamedEntityGraph(
        name = "order.detail",
        attributeNodes = {
                @NamedAttributeNode("user"),
                @NamedAttributeNode("orderItems"),
                @NamedAttributeNode("shipping")
        }
)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime orderDate;
    private PaymentMethod paymentMethod;
    private OrderStatus status;
    private BigDecimal total;
    @OneToOne(cascade = CascadeType.ALL,mappedBy = "order")
    private Shipping shipping;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(orphanRemoval = true,cascade = CascadeType.ALL,mappedBy = "order")
    private List<OrderItem> orderItems;

    private void setOrderTotal(){
        for(OrderItem orderItem: orderItems){
            total = total.add(orderItem.getSubTotal());
        }
    }
}
