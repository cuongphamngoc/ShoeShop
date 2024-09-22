package com.cuongpn.shoeshop.service.Impl;

import com.cuongpn.shoeshop.entity.CartItem;
import com.cuongpn.shoeshop.entity.Order;
import com.cuongpn.shoeshop.entity.OrderItem;
import com.cuongpn.shoeshop.repository.OrderItemRepository;
import com.cuongpn.shoeshop.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository orderItemRepository;
    @Override
    @Transactional
    public void addItemToOrder(Order order, List<CartItem> cartItemList) {
        List<OrderItem> orderItems = order.getOrderItems();
        for(CartItem cartItem : cartItemList){
            OrderItem orderItem = createNewOrderItem(cartItem, order);
            orderItems.add(orderItem);
        }
        setOrderTotal(order);

    }
    private OrderItem createNewOrderItem(CartItem cartItem,Order order){
        OrderItem orderItem = OrderItem.builder().qty(cartItem.getQty())
                .price(cartItem.getPrice())
                .totalPrice(cartItem.getTotalPrice())
                .order(order)
                .productSize(cartItem.getProductSize())
                .build();
        return orderItemRepository.save(orderItem);
    }
    private void setOrderTotal(Order order){
        List<OrderItem> orderItems = order.getOrderItems();
        BigDecimal total = new BigDecimal(0);
        for(OrderItem orderItem:orderItems){
            total = total.add(orderItem.getTotalPrice());
        }
        order.setTotal(total);
    }
}
