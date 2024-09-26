package com.cuongpn.shoeshop.enums;

public enum OrderStatus {
    PENDING,    // Đơn hàng đang chờ xử lý
    PAID,       // Đơn hàng đã được thanh toán
    SHIPPED,    // Đơn hàng đã được giao
    DELIVERED,  // Đơn hàng đã được giao thành công
    CANCELLED,  // Đơn hàng đã bị hủy
    FAILED,      // Thanh toán thất bại
    CASH_ON_DELIVERY
}
