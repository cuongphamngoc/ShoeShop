package com.cuongpn.shoeshop.repository;

import com.cuongpn.shoeshop.entity.Cart;
import com.cuongpn.shoeshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart,Long> {

    public Optional<Cart> findByUser(User user);
}
