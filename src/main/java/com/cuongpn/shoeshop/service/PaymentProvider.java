package com.cuongpn.shoeshop.service;

import com.cuongpn.shoeshop.entity.Order;

public interface PaymentProvider {
    public String createPayment(Order order);

    public String charge(Order order);
}
