package com.cuongpn.shoeshop.service.Impl;

import com.cuongpn.shoeshop.entity.Shipping;
import com.cuongpn.shoeshop.repository.ShippingRepository;
import com.cuongpn.shoeshop.service.ShippingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ShippingServiceImpl implements ShippingService {
    private final ShippingRepository shippingRepository;
    @Override
    @Transactional
    public Shipping saveShipping(Shipping shipping) {
        return shippingRepository.save(shipping);
    }
}
