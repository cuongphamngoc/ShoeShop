package com.cuongpn.shoeshop.service.Impl;

import com.cuongpn.shoeshop.entity.Order;
import com.cuongpn.shoeshop.service.PaymentProvider;
import org.springframework.stereotype.Service;

@Service(ZaloPayPaymentProvider.BEAN_ID)
public class ZaloPayPaymentProvider implements PaymentProvider {
    public static final String BEAN_ID = "zaloPayPaymentProvider";



    @Override
    public String createPayment(Order order) {
        return null;
    }

    @Override
    public String charge(Order order) {
        return null;
    }
}
