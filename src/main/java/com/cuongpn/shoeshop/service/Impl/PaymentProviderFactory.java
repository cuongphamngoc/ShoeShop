package com.cuongpn.shoeshop.service.Impl;

import com.cuongpn.shoeshop.enums.PaymentMethod;
import com.cuongpn.shoeshop.service.PaymentProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentProviderFactory {
    private final Map<String, PaymentProvider> paymentProviderMap;

    @Autowired
    public PaymentProviderFactory(List<PaymentProvider> paymentProviders) {
        paymentProviderMap = paymentProviders.stream()
                .collect(Collectors.toMap(
                        provider -> provider.getClass().getAnnotation(Service.class).value(),
                        provider -> provider
                ));
    }

    public PaymentProvider getPaymentProvider(PaymentMethod paymentProviderType) {
        PaymentProvider provider = paymentProviderMap.get(paymentProviderType.getValue());
        if (provider == null) {
            throw new IllegalArgumentException("No payment provider found for type: " + paymentProviderType);
        }
        return provider;
    }
}
