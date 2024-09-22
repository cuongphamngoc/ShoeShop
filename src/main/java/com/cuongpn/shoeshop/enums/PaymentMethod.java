package com.cuongpn.shoeshop.enums;

public enum PaymentMethod {
    CASH("cashPaymentProvider"),       // Thanh toán bằng tiền mặt
    STRIPE("stripePaymentProvider"),   // Thanh toán qua Stripe
    VNPAY("vNPayPaymentProvider"),     // Thanh toán qua VNPay
    ZALOPAY("zaloPayPaymentProvider"); // Thanh toán qua ZaloPay

    private final String value;

    PaymentMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

