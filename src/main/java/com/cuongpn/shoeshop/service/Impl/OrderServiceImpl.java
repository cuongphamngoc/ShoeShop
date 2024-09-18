package com.cuongpn.shoeshop.service.Impl;

import com.cuongpn.shoeshop.dto.CartItemDTO;
import com.cuongpn.shoeshop.entity.*;
import com.cuongpn.shoeshop.enums.OrderStatus;
import com.cuongpn.shoeshop.enums.PaymentMethod;
import com.cuongpn.shoeshop.mapper.CartItemMapper;
import com.cuongpn.shoeshop.repository.OrderRepository;
import com.cuongpn.shoeshop.service.CartService;
import com.cuongpn.shoeshop.service.OrderItemService;
import com.cuongpn.shoeshop.service.OrderService;
import com.cuongpn.shoeshop.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final UserService userService;
    private final OrderItemService orderItemService;
    private final CartItemMapper cartItemMapper;



    @Override
    public List<Order> findByUser(User user) {
        return orderRepository.findByUser(user);
    }

    @Override
    public Order createOrder(List<CartItemDTO> cartItems, Long addressId, PaymentMethod paymentMethod, Principal principal) {
        User user = userService.findByUserName(principal.getName());
        Set<Address> addresses = user.getAddress();
        Optional<Address> addressOptional = addresses.stream().filter(address1 -> address1.getId().equals(addressId)).findFirst();
        if(addressOptional.isEmpty()) throw  new RuntimeException("No address found");
        Address address = addressOptional.get();
        Cart cart  = cartService.findCartByUser(user);
        List<CartItem> allItemsOfCart = cart.getCartItems();
        Set<CartItemDTO> cartItemDTOSet = new HashSet<>(cartItems);
        for(CartItemDTO cartItemDTO:cartItemDTOSet){
            System.out.println(cartItemDTO);
        }
        List<CartItem> selectedCartItem = allItemsOfCart.stream()
                .filter(cartItem -> {
                    CartItemDTO current = cartItemMapper.cartItemToCartItemDTO(cartItem);
                    System.out.println(current);
                    System.out.println(cartItemDTOSet.contains(current));
                    return cartItemDTOSet.contains(current);
                })
                .collect(Collectors.toList());

        allItemsOfCart.removeIf(cartItem -> cartItemDTOSet.contains(cartItemMapper.cartItemToCartItemDTO(cartItem)));

        cartService.saveCart(cart);
        Order order = Order.builder()
                .orderDate( LocalDateTime.now())
                .orderStatus(OrderStatus.PENDING)
                .paymentMethod(paymentMethod)
                .orderTotal(new BigDecimal(0))
                .orderItems(new ArrayList<>())
                .shippingDate(LocalDateTime.now().plusDays(7) )
                .build();
        Order res = orderRepository.save(order);

        res = orderItemService.addItemToOrder(res,selectedCartItem);
        orderRepository.save(res);
        return res;

    }
}
