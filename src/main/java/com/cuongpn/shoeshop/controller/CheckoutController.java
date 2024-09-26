package com.cuongpn.shoeshop.controller;

import com.cuongpn.shoeshop.dto.CartItemDTO;
import com.cuongpn.shoeshop.dto.ConfirmOrderRequest;
import com.cuongpn.shoeshop.entity.*;
import com.cuongpn.shoeshop.enums.OrderStatus;
import com.cuongpn.shoeshop.enums.PaymentMethod;
import com.cuongpn.shoeshop.mapper.CartItemMapper;
import com.cuongpn.shoeshop.service.*;
import com.cuongpn.shoeshop.service.Impl.PaymentProviderFactory;
import com.cuongpn.shoeshop.util.HMACUtil;
import com.cuongpn.shoeshop.util.VNPAYUtil;

import com.cuongpn.shoeshop.util.ZaloPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
@Slf4j
public class CheckoutController {
    private final UserService userService;
    private final CartService cartService;
    private final CartItemMapper cartItemMapper;
    private final OrderService orderService;
    private final PaymentProviderFactory paymentProviderFactory;

    @PostMapping("/confirm-order")
    public String getCheckout(@RequestParam List<Long> selectedIds , Model model, Principal principal, RedirectAttributes redirectAttributes){
        System.out.println("Process.... " + selectedIds);
        User user  = userService.findByUserName(principal.getName());
        List<Address> addressList = user.getAddress().stream()
                .filter(address -> !address.getIsDeleted())
                .collect(Collectors.toList());;
        if (addressList.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Bạn cần thêm ít nhất một địa chỉ trước khi tiếp tục.");
            return "redirect:/user/add-address";
        }

        List<CartItem> cartItems = cartService.findCartByUser(user).getCartItems();
        List<CartItemDTO> filterCartItems = cartItems.stream()
                .filter(cartItem -> selectedIds.contains(cartItem.getId()))
                .map(cartItemMapper::toCartItemDTO)
                .toList();
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
    public String placeOrder(@Valid @ModelAttribute("confirmOrder") ConfirmOrderRequest confirmOrderRequest,BindingResult bindingResult, Model model, Principal principal) throws Exception {

        if(bindingResult.hasErrors()){
            User user  = userService.findByUserName(principal.getName());
            List<Address> addressList = new ArrayList<>(user.getAddress());
            addressList.sort(Comparator.comparingLong(Address::getId));
            model.addAttribute("addresses",addressList);
            return "confirm-order";
        }
        Order order = orderService.createOrder(confirmOrderRequest,principal);

        PaymentProvider paymentProvider = paymentProviderFactory.getPaymentProvider(confirmOrderRequest.getPaymentMethod());

        model.addAttribute("order",order);
        //return paymentPage;
        try{
            return paymentProvider.createPayment(order);
        }
        catch (Exception e){
            User user = userService.findByUserName(principal.getName());
            orderService.processFailOrder(order,cartService.findCartByUser(user));
            model.addAttribute("message","Order failed!");
            return "error";
        }
    }

}
