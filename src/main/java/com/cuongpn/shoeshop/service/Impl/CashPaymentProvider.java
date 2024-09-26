package com.cuongpn.shoeshop.service.Impl;

import com.cuongpn.shoeshop.entity.Order;
import com.cuongpn.shoeshop.entity.User;
import com.cuongpn.shoeshop.enums.OrderStatus;
import com.cuongpn.shoeshop.service.OrderService;
import com.cuongpn.shoeshop.service.PaymentProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service(CashPaymentProvider.BEAN_ID)
@RequiredArgsConstructor
public class CashPaymentProvider implements PaymentProvider {
    public static final String BEAN_ID = "cashPaymentProvider";
    private final OrderService orderService;


    @Override
    public String createPayment(Order order) {
        User user = order.getUser();
        orderService.handleCODOrder(user,order);
        return "redirect:/payment/completed?id=" + order.getId();
    }


}
