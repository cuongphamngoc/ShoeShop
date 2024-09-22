package com.cuongpn.shoeshop.service.Impl;

import com.cuongpn.shoeshop.entity.Order;
import com.cuongpn.shoeshop.entity.OrderItem;
import com.cuongpn.shoeshop.service.ExchangeRateService;
import com.cuongpn.shoeshop.service.PaymentProvider;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service(StripePaymentProvider.BEAN_ID)
@RequiredArgsConstructor
public class StripePaymentProvider implements PaymentProvider {
    public static final String BEAN_ID = "stripePaymentProvider";
    @Value("${stripe.api.key}")
    private String apiKey;
    private final ExchangeRateService exchangeRateService;

    @Value("${stripe.success.url}")
    private String successUrl;

    @Value("${stripe.cancel.url}")
    private String cancelUrl;


    @PostConstruct
    public void init() {
        Stripe.apiKey = apiKey;
    }

    @Override
    public String createPayment(Order order) throws StripeException {
        BigDecimal exchangeRate = exchangeRateService.getExchangeRate("VND", "USD");
        System.out.println(exchangeRate);

        SessionCreateParams.Builder sessionBuilder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:8080/callback/success?id=" + order.getId())
                .setCancelUrl("http://localhost:8080/callback/cancel?id=" + order.getId());

        List<OrderItem> items = order.getOrderItems();
        for (OrderItem item : items) {
            BigDecimal priceInUSD = item.getPrice().multiply(exchangeRate);
            SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                    .setQuantity(item.getQty().longValue())
                    .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("usd")
                                    .setUnitAmount(priceInUSD.movePointRight(2).longValue())
                                    .setProductData(
                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                    .setName(item.getProductSize().getProduct().getTitle())
                                                    .build())
                                    .build())
                    .build();
            sessionBuilder.addLineItem(lineItem);
        }

        SessionCreateParams sessionCreateParams = sessionBuilder.build();
        Session session = Session.create(sessionCreateParams);
        return "redirect:" + session.getUrl();

    }


}
