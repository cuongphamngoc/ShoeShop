package com.cuongpn.shoeshop.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderCheck {
    private boolean checkOrderId = true; // vnp_TxnRef exists in your database
    private boolean checkAmount = true; // vnp_Amount is valid
    private boolean checkOrderStatus = true; // PaymnentStatus = 0 (pending)
}
