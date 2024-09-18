package com.cuongpn.shoeshop.service;

import com.cuongpn.shoeshop.dto.AddCartRequest;
import com.cuongpn.shoeshop.dto.UpdateQuantityRequest;
import com.cuongpn.shoeshop.entity.Cart;
import com.cuongpn.shoeshop.entity.User;

import java.util.List;

public interface CartService {

    public Cart findCartByUser(User user);

    public void saveCart(Cart cart);

    public  int getItemsNumber(User user);
    public void deleteCartItem(List<Long> cartIds, Cart cart);

    public void updateCartItem(Cart cart, UpdateQuantityRequest updateQuantityRequest);

}
