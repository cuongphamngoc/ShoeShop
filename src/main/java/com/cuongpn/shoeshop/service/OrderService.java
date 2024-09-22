package com.cuongpn.shoeshop.service;

import com.cuongpn.shoeshop.dto.CartItemDTO;
import com.cuongpn.shoeshop.dto.ConfirmOrderRequest;
import com.cuongpn.shoeshop.entity.Cart;
import com.cuongpn.shoeshop.entity.Order;
import com.cuongpn.shoeshop.entity.User;
import com.cuongpn.shoeshop.enums.PaymentMethod;
import org.aspectj.weaver.ast.Or;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    List<Order> findByUser(User user);

    Optional<Order> findById(Long id);

    void cancelOrder(Order order, Cart cart);
    void saveOrder(Order order);

    Order createOrder(ConfirmOrderRequest confirmOrderRequest, Principal principal);
}
