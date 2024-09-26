package com.cuongpn.shoeshop.service;

import com.cuongpn.shoeshop.dto.AddCartRequest;

import com.cuongpn.shoeshop.entity.User;

public interface CartItemService {
    int addItemToCart(AddCartRequest addCartRequest, User user);
}
