package com.cuongpn.shoeshop.service.Impl;

import com.cuongpn.shoeshop.repository.OrderRepository;
import com.cuongpn.shoeshop.service.CheckoutService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {
    private final OrderRepository orderRepository;
    private final PaymentProviderFactory paymentProviderFactory;
}
