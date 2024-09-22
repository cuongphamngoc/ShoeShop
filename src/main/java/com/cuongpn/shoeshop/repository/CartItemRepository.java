package com.cuongpn.shoeshop.repository;

import com.cuongpn.shoeshop.entity.CartItem;
import com.cuongpn.shoeshop.entity.ProductSize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Long> {
    Optional<CartItem> findByProductSize(ProductSize productSize);
}
