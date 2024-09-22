package com.cuongpn.shoeshop.service;

import com.cuongpn.shoeshop.dto.UpdateQuantityRequest;
import com.cuongpn.shoeshop.entity.Cart;
import com.cuongpn.shoeshop.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CartService {

    Cart findCartByUser(User user);

    void saveCart(Cart cart);

    int getItemsNumber(User user);
    ResponseEntity<?> deleteCartItem(List<Long> cartIds, User user);

    ResponseEntity<?> updateCartItem(User user, UpdateQuantityRequest updateQuantityRequest);
}
