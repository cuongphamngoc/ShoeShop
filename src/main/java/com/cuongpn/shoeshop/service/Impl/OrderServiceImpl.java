package com.cuongpn.shoeshop.service.Impl;

import com.cuongpn.shoeshop.dto.CartItemDTO;
import com.cuongpn.shoeshop.dto.ConfirmOrderRequest;
import com.cuongpn.shoeshop.entity.*;
import com.cuongpn.shoeshop.enums.OrderStatus;
import com.cuongpn.shoeshop.enums.PaymentMethod;
import com.cuongpn.shoeshop.mapper.CartItemMapper;
import com.cuongpn.shoeshop.repository.OrderRepository;
import com.cuongpn.shoeshop.repository.ShippingRepository;
import com.cuongpn.shoeshop.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final UserService userService;
    private final OrderItemService orderItemService;
    private final CartItemMapper cartItemMapper;
    private final ShippingService shippingService;
    private static final int SHIPPING_DAY = 7;



    @Override
    public List<Order> findByUser(User user) {
        return orderRepository.findByUser(user);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    public void cancelOrder(Order order,Cart cart) {
        order.setStatus(OrderStatus.CANCELLED);
        List<CartItem> cartItems = cart.getCartItems();
        List<OrderItem> orderItems = order.getOrderItems();
        for(OrderItem orderItem:orderItems){
            CartItem cartItem = CartItem.builder()
                    .cart(cart)
                    .productSize(orderItem.getProductSize())
                    .price(orderItem.getPrice())
                    .qty(orderItem.getQty())
                    .totalPrice(orderItem.getTotalPrice())
                    .build();
            cartItems.add(cartItem);
        }
        cartService.saveCart(cart);
        orderRepository.save(order);

    }

    @Override
    public void saveOrder(Order order) {
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order createOrder(ConfirmOrderRequest confirmOrderRequest, Principal principal) {
        User user = userService.findByUserName(principal.getName());
        Order newOrder = createAndSaveNewOrder(user,confirmOrderRequest.getPaymentMethod());
        createShipment(newOrder,confirmOrderRequest,user);
        Cart cart  = cartService.findCartByUser(user);
        addOrderItemsToOrder(newOrder,confirmOrderRequest,cart);
        return orderRepository.save(newOrder);

    }
    private Order createAndSaveNewOrder(User user,PaymentMethod paymentMethod){
        LocalDateTime orderDate = LocalDateTime.now();
        Order order = Order.builder()
                .orderDate(orderDate)
                .status(OrderStatus.PENDING)
                .paymentMethod(paymentMethod)
                .total(new BigDecimal(0))
                .orderItems(new ArrayList<>())
                .user(user)
                .build();
        return orderRepository.save(order);
    }
    private void createShipment(Order order,ConfirmOrderRequest confirmOrderRequest, User user){
        Set<Address> addresses = user.getAddress();
        Long addressId =confirmOrderRequest.getAddressId();
        Address address = addresses.stream()
                .filter(addr-> Objects.equals(addr.getId(), addressId))
                .findFirst().orElseThrow(()-> new RuntimeException("Address not found with id "+ addressId));
        LocalDateTime shippingDate = LocalDateTime.now().plusDays(SHIPPING_DAY);
        Shipping shipping = Shipping.builder()
                .name(confirmOrderRequest.getFullName())
                .phoneNumber(confirmOrderRequest.getPhoneNumber())
                .address(address)
                .order(order)
                .shippingDate(shippingDate)
                .build();
        Shipping current = shippingService.saveShipping(shipping);
        order.setShipping(current);
    }
    private void addOrderItemsToOrder(Order order, ConfirmOrderRequest confirmOrderRequest, Cart cart){
        List<CartItem> allItemsOfCart = cart.getCartItems();
        Set<CartItemDTO> cartItemDTOSet = new HashSet<>(confirmOrderRequest.getCartItems());
        List<CartItem> selectedCartItem = allItemsOfCart.stream()
                .filter(current ->
                    cartItemDTOSet.contains(cartItemMapper.cartItemToCartItemDTO(current))
                )
                .toList();
        allItemsOfCart.removeIf(selectedCartItem::contains);
        orderItemService.addItemToOrder(order,selectedCartItem);


    }
}
