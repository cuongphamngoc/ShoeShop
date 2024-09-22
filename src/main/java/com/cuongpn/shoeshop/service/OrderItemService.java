package com.cuongpn.shoeshop.service;

import com.cuongpn.shoeshop.entity.CartItem;
import com.cuongpn.shoeshop.entity.Order;

import java.util.List;

public interface OrderItemService {
    void addItemToOrder(Order order, List<CartItem> cartItemList);
}
