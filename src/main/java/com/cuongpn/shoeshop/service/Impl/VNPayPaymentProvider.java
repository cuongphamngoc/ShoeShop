package com.cuongpn.shoeshop.service.Impl;

import com.cuongpn.shoeshop.entity.Order;
import com.cuongpn.shoeshop.service.PaymentProvider;
import org.springframework.stereotype.Service;

@Service(VNPayPaymentProvider.BEAN_ID)
public class VNPayPaymentProvider implements PaymentProvider {
    public static final String BEAN_ID = "vNPayPaymentProvider";


    @Override
    public String createPayment(Order order) {
        return null;
    }

    @Override
    public String charge(Order order) {
        return null;
    }
}
