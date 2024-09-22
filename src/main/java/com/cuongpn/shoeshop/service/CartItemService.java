package com.cuongpn.shoeshop.service;

import com.cuongpn.shoeshop.dto.AddCartRequest;
import com.cuongpn.shoeshop.entity.Cart;

import com.cuongpn.shoeshop.entity.User;
import org.springframework.http.ResponseEntity;
public interface CartItemService {
    ResponseEntity<?> addItemToCart(AddCartRequest addCartRequest, User user);
}
