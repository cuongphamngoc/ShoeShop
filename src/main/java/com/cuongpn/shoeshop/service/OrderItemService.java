package com.cuongpn.shoeshop.service;

import com.cuongpn.shoeshop.entity.CartItem;
import com.cuongpn.shoeshop.entity.Order;

import java.util.List;

public interface OrderItemService {
    public Order addItemToOrder(Order order, List<CartItem> cartItemList);
}
