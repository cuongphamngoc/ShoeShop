package com.cuongpn.shoeshop.controller;

import com.cuongpn.shoeshop.dto.CartItemDTO;
import com.cuongpn.shoeshop.dto.ConfirmOrderRequest;
import com.cuongpn.shoeshop.entity.Address;
import com.cuongpn.shoeshop.entity.CartItem;
import com.cuongpn.shoeshop.entity.Order;
import com.cuongpn.shoeshop.entity.User;
import com.cuongpn.shoeshop.enums.PaymentMethod;
import com.cuongpn.shoeshop.mapper.CartItemMapper;
import com.cuongpn.shoeshop.service.*;
import com.cuongpn.shoeshop.service.Impl.PaymentProviderFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@AllArgsConstructor
public class CheckoutController {
    private final CheckoutService checkoutService;
    private final UserService userService;
    private final CartService cartService;
    private final CartItemMapper cartItemMapper;
    private final OrderService orderService;
    private final PaymentProviderFactory paymentProviderFactory;
    @PostMapping("/confirm-order")
    public String getCheckout(@RequestParam List<Long> selectedIds , Model model, Principal principal, RedirectAttributes redirectAttributes){
        System.out.println("Process.... " + selectedIds);
        User user  = userService.findByUserName(principal.getName());
        List<Address> addressList = new ArrayList<>(user.getAddress());
        addressList.sort((a,b)->Long.compare(a.getId(),b.getId()));
        if (addressList.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Bạn cần thêm ít nhất một địa chỉ trước khi tiếp tục.");
            return "redirect:/user/add-address";
        }

        List<CartItem> cartItems = cartService.findCartByUser(user).getCartItems();
        List<CartItemDTO> filterCartItems = cartItems.stream().filter(cartItem -> selectedIds.contains(cartItem.getId())).map(cartItemMapper::cartItemToCartItemDTO).toList();
        BigDecimal totalPrice = new BigDecimal(0);
        for(CartItemDTO cart:filterCartItems){
            totalPrice = totalPrice.add(cart.getTotalPrice());
        }
        ConfirmOrderRequest confirmOrderRequest = new ConfirmOrderRequest();
        confirmOrderRequest.setCartItems(filterCartItems);
        confirmOrderRequest.setPaymentMethod(PaymentMethod.ZALOPAY);
        model.addAttribute("confirmOrder",confirmOrderRequest);
        model.addAttribute("totalPrice",totalPrice);
        model.addAttribute("cartItems",filterCartItems);
        model.addAttribute("addresses",addressList);
        return "confirm-order";
    }
    @PostMapping("/place-order")
    public String placeOrder(@Valid @ModelAttribute("confirmOrder") ConfirmOrderRequest confirmOrderRequest,BindingResult bindingResult, Model model, Principal principal) throws JsonProcessingException {

        if(bindingResult.hasErrors()){
            User user  = userService.findByUserName(principal.getName());
            List<Address> addressList = new ArrayList<>(user.getAddress());
            addressList.sort((a,b)->Long.compare(a.getId(),b.getId()));
            model.addAttribute("addresses",addressList);
            return "confirm-order";
        }
        Order order = orderService.createOrder(confirmOrderRequest.getCartItems(), confirmOrderRequest.getAddressId(), confirmOrderRequest.getPaymentMethod(),principal);

        PaymentProvider paymentProvider = paymentProviderFactory.getPaymentProvider(confirmOrderRequest.getPaymentMethod());

        model.addAttribute("order",order);
        //return paymentPage;
        return paymentProvider.createPayment(order);
    }
    @PostMapping("/callback/stripe")
    public String placeOrderSuccess(){
        return "checkout-completed-order";
    }
}
