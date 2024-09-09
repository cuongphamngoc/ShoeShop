package com.cuongpn.shoeshop.util;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyUtil {
    public static String formatCurrency(Long amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return formatter.format(amount);
    }
}