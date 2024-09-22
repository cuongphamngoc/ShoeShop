package com.cuongpn.shoeshop.service;

import com.cuongpn.shoeshop.entity.Order;
import com.stripe.exception.StripeException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public interface PaymentProvider {
    String createPayment(Order order) throws Exception;

}
