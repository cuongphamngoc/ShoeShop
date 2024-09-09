package com.cuongpn.shoeshop.service;

import com.cuongpn.shoeshop.entity.Order;
import com.cuongpn.shoeshop.entity.User;

import java.util.List;

public interface OrderService {
    public List<Order> findByUser(User user);

}
