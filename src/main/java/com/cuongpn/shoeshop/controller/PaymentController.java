package com.cuongpn.shoeshop.controller;

import com.cuongpn.shoeshop.entity.Cart;
import com.cuongpn.shoeshop.entity.Order;
import com.cuongpn.shoeshop.entity.User;
import com.cuongpn.shoeshop.enums.OrderStatus;
import com.cuongpn.shoeshop.service.CartService;
import com.cuongpn.shoeshop.service.OrderService;
import com.cuongpn.shoeshop.util.HMACUtil;
import com.cuongpn.shoeshop.util.OrderCheck;
import com.cuongpn.shoeshop.util.VNPAYUtil;
import com.cuongpn.shoeshop.util.ZaloPayUtil;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payment")
@Slf4j
public class PaymentController {
    private final OrderService orderService;
    private final CartService cartService;
    @Value("${stripe.webhook.endpoint.secret}")
    private String stripeEndpointSecret;

    @GetMapping("/completed")
    public String handleSuccessPayment(@RequestParam("id") Long id, Model model) {
        Order order = orderService.findById(id).orElseThrow();
        User user = order.getUser();
        System.out.println(order.getStatus());
        model.addAttribute("order", order);
        model.addAttribute("shoppingCartItemNumber", cartService.getItemsNumber(user));
        return "payment-result";
    }

    @GetMapping("/cancel")
    public String handleCancelPayment(@RequestParam("id") Long id, Model model) {
        Order order = orderService.findById(id).orElseThrow();
        model.addAttribute("order", order);
        return "redirect:/shopping-cart";
    }

    @GetMapping("/vnpay-return")
    public String handleVNPAYPayment(HttpServletRequest request, Model model) {
        String orderId = request.getParameter("vnp_TxnRef");
        return "redirect:/payment/completed?id=" + orderId;
    }

    @GetMapping("/vnpay-callback")
    public @ResponseBody ResponseEntity<?> handleVNPAYPaymentIPN(HttpServletRequest request) {
        try {
            Map<String, String> fields = extractFields(request);
            String vnp_SecureHash = request.getParameter("vnp_SecureHash");

            if (isChecksumValid(fields, vnp_SecureHash)) {
                return processTransaction(fields);
            } else {
                return createResponse("97", "Invalid Checksum");
            }
        } catch (Exception e) {
            return createResponse("99", "Unknown error");
        }
    }

    private Map<String, String> extractFields(HttpServletRequest request) throws Exception {
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = params.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                fields.put(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII),
                        URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
            }
        }
        fields.remove("vnp_SecureHashType");
        fields.remove("vnp_SecureHash");
        return fields;
    }

    private boolean isChecksumValid(Map<String, String> fields, String vnp_SecureHash) {
        String signValue = VNPAYUtil.hashAllFields(fields);
        return signValue.equals(vnp_SecureHash);
    }

    private ResponseEntity<?> processTransaction(Map<String, String> fields) {

        String orderId = fields.get("vnp_TxnRef");
        OrderCheck orderCheck = new OrderCheck();
        Order order = orderService.findById(Long.parseLong(orderId)).orElseGet(() -> {
            orderCheck.setCheckAmount(false);
            orderCheck.setCheckOrderId(false);
            orderCheck.setCheckOrderStatus(false);
            return null;
        });

        if (order != null) {
            orderCheck.setCheckOrderId(true);
            Long amount = Long.parseLong(fields.get("vnp_Amount")) / 100;
            System.out.println(amount);

            orderCheck.setCheckAmount(amount == order.getTotal().longValue()); // Kiểm tra số tiền
            orderCheck.setCheckOrderStatus(order.getStatus().equals(OrderStatus.PENDING));
        }


        if (!orderCheck.isCheckOrderId()) {
            return createResponse("01", "Order not Found");

        }

        if (!orderCheck.isCheckAmount()) {
            return createResponse("04", "Invalid Amount");

        }

        if (!orderCheck.isCheckOrderStatus()) {
            return createResponse("02", "Order already confirmed");
        }
        User user = order.getUser();
        Cart cart = cartService.findCartByUser(user);
        System.out.println(fields.get("vnp_ResponseCode"));
        if ("00".equals(fields.get("vnp_ResponseCode"))) {

            orderService.handleSuccessOrder(user, order);
        } else if ("24".equals(fields.get("vnp_ResponseCode"))) {
            orderService.handleCancelOrder(Long.parseLong(orderId));
        } else {
            // Update PaymnentStatus = 2 in your Database
            orderService.processFailOrder(order, cart);
        }
        return createResponse("00", "Confirm Success");
    }

    private ResponseEntity<?> createResponse(String rspCode, String message) {

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("RspCode", rspCode);
        responseBody.put("Message", message);
        System.out.println(responseBody);
        return ResponseEntity.ok(responseBody);

    }

    @GetMapping("/zalopay-result")
    public String handleZaloPayMethodResult(@RequestParam("id") Long orderId) {
        Order order = orderService.findById(orderId).orElseThrow();
        if (!order.getStatus().equals(OrderStatus.PAID)) {
            orderService.handleCancelOrder(orderId);
        }
        return "redirect:/payment/completed?id=" + orderId;

    }

    @PostMapping("/zalopay-callback")
    public @ResponseBody String handleZaloPayPaymentCallback(@RequestBody String jsonStr, Model model) throws NoSuchAlgorithmException, InvalidKeyException {
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
            String mac = HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, ZaloPayUtil.config.get("key2"), dataStr);

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
                String OrderId = data.getString("app_trans_id");
                Long Id = Long.parseLong(OrderId.split("_")[1]);
                Order order = orderService.findById(Id).orElseThrow();
                User user = order.getUser();
                orderService.handleSuccessOrder(user, order);

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
    @GetMapping("/stripe-cancel")
    public String handleStripeCancel(@RequestParam("id") Long id){
        orderService.handleCancelOrder(id);
        return "redirect:/payment/completed?id=" + id;
    }
    @PostMapping("/stripe-webhook")
    public @ResponseBody ResponseEntity<?> handleStripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        try {

            Event event = Webhook.constructEvent(payload, sigHeader, stripeEndpointSecret);
            EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();

            StripeObject stripeObject = null;
            if (dataObjectDeserializer.getObject().isPresent()) {
                stripeObject = dataObjectDeserializer.getObject().get();
            } else {
                return ResponseEntity.badRequest().build();
            }

            if ("checkout.session.completed".equals(event.getType())) {
                Session session = (Session) stripeObject;
                handleCheckoutSessionCompleted(session);
            } else {
                System.out.println("Unhandled event type: " + event.getType());
            }

            return ResponseEntity.ok("");

        } catch (SignatureVerificationException e) {
            return ResponseEntity.badRequest().build();
        }


    }

    private void handleCheckoutSessionCompleted(Session session) {
        String orderId = session.getMetadata().get("order_id");
        System.out.println(orderId);
        Order order = orderService.findById(Long.parseLong(orderId)).orElseThrow();
        User user = order.getUser();
        orderService.handleSuccessOrder(user, order);
    }
}
