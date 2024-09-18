package com.cuongpn.shoeshop.service;

import com.cuongpn.shoeshop.dto.AddCartRequest;
import com.cuongpn.shoeshop.entity.Cart;
import com.cuongpn.shoeshop.entity.CartItem;
import com.cuongpn.shoeshop.entity.ProductSize;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface CartItemService {
    public ResponseEntity<?> addItemToCart(AddCartRequest addCartRequest, Cart cart);
    public Optional<CartItem> findByProductSize(ProductSize productSize);

}
