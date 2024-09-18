package com.cuongpn.shoeshop.service;

import com.cuongpn.shoeshop.dto.CartItemDTO;
import com.cuongpn.shoeshop.entity.Order;
import com.cuongpn.shoeshop.entity.User;
import com.cuongpn.shoeshop.enums.PaymentMethod;

import java.security.Principal;
import java.util.List;

public interface OrderService {
    public List<Order> findByUser(User user);


    Order createOrder(List<CartItemDTO> cartItems, Long addressId, PaymentMethod paymentMethod, Principal principal);
}
