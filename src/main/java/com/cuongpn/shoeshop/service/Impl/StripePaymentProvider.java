package com.cuongpn.shoeshop.service.Impl;

import com.cuongpn.shoeshop.entity.Order;
import com.cuongpn.shoeshop.service.ExchangeRateService;
import com.cuongpn.shoeshop.service.PaymentProvider;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.billingportal.Session;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(StripePaymentProvider.BEAN_ID)
public class StripePaymentProvider implements PaymentProvider {
    public static final String BEAN_ID = "stripePaymentProvider";
    @Value("${stripe.api.key}")
    private String apiKey;
    private final ExchangeRateService exchangeRateService;

    @Value("${stripe.success.url}")
    private String successUrl;

    @Value("${stripe.cancel.url}")
    private String cancelUrl;
    @Autowired
    public StripePaymentProvider(ExchangeRateService exchangeRateService){
        this.exchangeRateService = exchangeRateService;
    }

    @PostConstruct
    public void init(){
        Stripe.apiKey = apiKey;
    }

    @Override
    public String createPayment(Order order) {
        try {
            BigDecimal exchangeRate = exchangeRateService.getExchangeRate("VND", "USD");
            BigDecimal amountInUSD = order.getOrderTotal().multiply(exchangeRate);

            List<Object> paymentMethodTypes = Arrays.asList("card");
            Map<String, Object> params = new HashMap<>();
            params.put("payment_method_types", paymentMethodTypes);
            params.put("line_items", Arrays.asList(
                    Map.of(
                            "price_data", Map.of(
                                    "currency", "usd",
                                    "product_data", Map.of(
                                            "name", "Order #" + order.getId()
                                    ),
                                    "unit_amount", amountInUSD.multiply(new BigDecimal("100")).intValue()
                            ),
                            "quantity", 1
                    )
            ));
            params.put("mode", "payment");
            params.put("success_url", successUrl + "?orderId=" + order.getId());
            params.put("cancel_url", cancelUrl + "?orderId=" + order.getId());

            Session session = Session.create(params);
            return session.getUrl();
        } catch (StripeException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String charge(Order order) {
        return "";
    }
}
