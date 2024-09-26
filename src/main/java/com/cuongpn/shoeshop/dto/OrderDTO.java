package com.cuongpn.shoeshop.dto;

import com.cuongpn.shoeshop.entity.OrderItem;
import com.cuongpn.shoeshop.entity.Shipping;
import com.cuongpn.shoeshop.entity.User;
import com.cuongpn.shoeshop.enums.OrderStatus;
import com.cuongpn.shoeshop.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDTO {
    private Long id;
    private LocalDateTime orderDate;
    private PaymentMethod paymentMethod;
    private OrderStatus status;
    private BigDecimal total;
    private LocalDateTime shippingDate;
    private Shipping shipping;
}
