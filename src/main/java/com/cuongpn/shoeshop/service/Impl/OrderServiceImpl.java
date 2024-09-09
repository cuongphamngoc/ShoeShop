package com.cuongpn.shoeshop.service.Impl;

import com.cuongpn.shoeshop.entity.Order;
import com.cuongpn.shoeshop.entity.User;
import com.cuongpn.shoeshop.repository.OrderRepository;
import com.cuongpn.shoeshop.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;


    @Override
    public List<Order> findByUser(User user) {
        return orderRepository.findByUser(user);
    }
}
