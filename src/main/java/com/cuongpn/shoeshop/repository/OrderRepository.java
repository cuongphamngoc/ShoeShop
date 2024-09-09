package com.cuongpn.shoeshop.repository;

import com.cuongpn.shoeshop.entity.Order;
import com.cuongpn.shoeshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByUser(User user);
}
