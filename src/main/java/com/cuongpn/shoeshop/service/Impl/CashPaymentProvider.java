package com.cuongpn.shoeshop.service.Impl;

import com.cuongpn.shoeshop.entity.Order;
import com.cuongpn.shoeshop.service.PaymentProvider;
import org.springframework.stereotype.Service;

@Service(CashPaymentProvider.BEAN_ID)
public class CashPaymentProvider implements PaymentProvider {
    public static final String BEAN_ID = "cashPaymentProvider";


    @Override
    public String createPayment(Order order) {
        return "cash-completed-order";
    }


}
