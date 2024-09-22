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
        return paymentProvider.createPayment(order);
    }
    @PostMapping("/callback/stripe")
    public String placeOrderSuccess(){
        return "checkout-completed-order";
    }
    @GetMapping("/callback/success")
    public String handleSuccessPayment(@RequestParam("id") Long id, Model model,Principal principal){
        Order order = orderService.findById(id).orElseThrow();
        User user = userService.findByUserName(principal.getName());
        order.setStatus(OrderStatus.PAID);
        orderService.saveOrder(order);
        model.addAttribute("order",order);
        model.addAttribute("shoppingCartItemNumber", cartService.getItemsNumber(user) );
        return "checkout-completed-order";
    }
    @GetMapping("/callback/cancel")
    public String handleCancelPayment(@RequestParam("id") Long id, Model model,Principal principal){
        User user = userService.findByUserName(principal.getName());
        Cart cart = cartService.findCartByUser(user);
        Order order = orderService.findById(id).orElseThrow();
        orderService.cancelOrder(order,cart);
        model.addAttribute("order",order);
        return "redirect:/shopping-cart";
    }
    @GetMapping("/vnpay-payment-return")
    public String handelVNPAYPayment(HttpServletRequest request, Model model){
        int paymentStatus = VNPAYUtil.orderReturn(request);
        String OrderId  = request.getParameter("vnp_TxnRef");

        if(paymentStatus == 1){
            Order order = orderService.findById(Long.parseLong(OrderId)).orElseThrow();
            order.setStatus(OrderStatus.PAID);
            orderService.saveOrder(order);
            model.addAttribute("order",order);
            return "checkout-completed-order";
        }



        return "redirect:/callback/cancel&id="+OrderId;
    }
    @PostMapping("/zalo-payment-return")
    public String handleZaloPayPayment(@RequestBody String jsonStr, Model model) throws NoSuchAlgorithmException, InvalidKeyException {
        JSONObject result = new JSONObject();
        String key2 = "eG4r0GcoNtRGbO8";
        Mac HmacSHA256;
        HmacSHA256 = Mac.getInstance("HmacSHA256");
        HmacSHA256.init(new SecretKeySpec(key2.getBytes(), "HmacSHA256"));
        try {
            JSONObject cbdata = new JSONObject(jsonStr);
            String dataStr = cbdata.getString("data");
            String reqMac = cbdata.getString("mac");

            byte[] hashBytes = HmacSHA256.doFinal(dataStr.getBytes());
            String mac  = HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, ZaloPayUtil.config.get("key2"), dataStr);

            // kiểm tra callback hợp lệ (đến từ ZaloPay server)
            if (!reqMac.equals(mac)) {
                // callback không hợp lệ
                result.put("return_code", -1);
                result.put("return_message", "mac not equal");
            } else {
                // thanh toán thành công
                // merchant cập nhật trạng thái cho đơn hàng

                JSONObject data = new JSONObject(dataStr);
                log.info("update order's status = success where app_trans_id = " + data.getString("app_trans_id"));
                String OrderId =  data.getString("app_trans_id");
                Long Id = Long.parseLong(OrderId.split("_")[1]);
                Order order = orderService.findById(Id).orElseThrow();
                order.setStatus(OrderStatus.PAID);
                orderService.saveOrder(order);

                result.put("return_code", 1);
                result.put("return_message", "success");
            }
        } catch (Exception ex) {
            result.put("return_code", 0); // ZaloPay server sẽ callback lại (tối đa 3 lần)
            result.put("return_message", ex.getMessage());
        }

        // thông báo kết quả cho ZaloPay server
        return result.toString();
    }





}
