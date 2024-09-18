package com.cuongpn.shoeshop.service.Impl;

import com.cuongpn.shoeshop.entity.CartItem;
import com.cuongpn.shoeshop.entity.Order;
import com.cuongpn.shoeshop.entity.OrderItem;
import com.cuongpn.shoeshop.repository.OrderItemRepository;
import com.cuongpn.shoeshop.service.OrderItemService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
@Service
@AllArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository orderItemRepository;
    @Override
    @Transactional
    public Order addItemToOrder(Order order, List<CartItem> cartItemList) {
        System.out.println("Processing.... " + cartItemList.size());
        List<OrderItem> orderItems = order.getOrderItems();
        for(CartItem cartItem : cartItemList){
            OrderItem orderItem = OrderItem.builder().qty(cartItem.getQty())
                    .price(cartItem.getPrice())
                    .totalPrice(cartItem.getTotalPrice())
                    .order(order)
                    .productSize(cartItem.getProductSize())
                    .build();
            OrderItem res = orderItemRepository.save(orderItem);
            orderItems.add(res);
        }
        BigDecimal total = new BigDecimal(0);
        for(OrderItem orderItem:orderItems){
            total = total.add(orderItem.getTotalPrice());
        }
        order.setOrderTotal(total);
        return order;
    }
}
