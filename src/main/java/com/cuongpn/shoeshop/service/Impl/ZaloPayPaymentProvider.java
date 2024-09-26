package com.cuongpn.shoeshop.service.Impl;

import com.cuongpn.shoeshop.entity.CartItem;
import com.cuongpn.shoeshop.entity.Order;
import com.cuongpn.shoeshop.entity.OrderItem;
import com.cuongpn.shoeshop.service.PaymentProvider;
import com.cuongpn.shoeshop.util.HMACUtil;
import com.cuongpn.shoeshop.util.ZaloPayUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static com.cuongpn.shoeshop.util.ZaloPayUtil.getCurrentTimeString;

@Service(ZaloPayPaymentProvider.BEAN_ID)
@RequiredArgsConstructor
public class ZaloPayPaymentProvider implements PaymentProvider {
    public static final String BEAN_ID = "zaloPayPaymentProvider";

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;
    @Value("${app.domain.url}")
    private String domainUrl;
    private Map<String, String> createEmbedData(Order orders) {
        Map<String, String> embedData = new HashMap<>();
        embedData.put("redirecturl", domainUrl + "/payment/zalopay-result?id=" + orders.getId());
        return embedData;
    }
    private List<Map<String,Object>> createItems(Order order){
        List<Map<String, Object>> items = new ArrayList<>();
        for(OrderItem orderItem : order.getOrderItems()){
            Map<String,Object> current = new HashMap<>();
            current.put("Id",orderItem.getId());
            current.put("Name",orderItem.getProductSize().getProduct().getTitle());
            current.put("Price",orderItem.getPrice());
            current.put("Qty",orderItem.getQty());
            current.put("Total",orderItem.getTotalPrice());
            items.add(current);
        }
        return items;
    }
    private Map<String, Object> createOrderMap(Map<String, String> config, Order orders, Map<String, String> embedData, List<Map<String, Object>> items) throws JsonProcessingException {
        Map<String, Object> order = new HashMap<>();
        order.put("app_id", Integer.parseInt(config.get("app_id")));
        order.put("app_trans_id", getCurrentTimeString("yyMMdd") + "_" + orders.getId());
        order.put("app_time", System.currentTimeMillis());
        order.put("app_user", "ZaloPayDemo");
        order.put("amount", orders.getTotal().longValue());
        order.put("description", "ZaloPayDemo - Thanh toán cho đơn hàng #" + getCurrentTimeString("yyMMdd") + "_" + orders.getId());
        order.put("callback_url", domainUrl + "/payment/zalopay-callback");
        order.put("item", objectMapper.writeValueAsString(items));
        order.put("embed_data", new JSONObject(embedData).toString());
        return order;
    }
    private String createDataString(Map<String, Object> order) {
        return String.join("|",
                order.get("app_id").toString(),
                order.get("app_trans_id").toString(),
                order.get("app_user").toString(),
                order.get("amount").toString(),
                order.get("app_time").toString(),
                order.get("embed_data").toString(),
                order.get("item").toString()
        );
    }
    private ResponseEntity<String> sendRequest(String endpoint, Map<String, Object> order) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(order, headers);
        return restTemplate.exchange(endpoint, HttpMethod.POST, request, String.class);
    }

    private String extractOrderUrl(String responseBody) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("order_url").asText();
    }


    @Override
    public String createPayment(Order orders) throws Exception {
        Map<String, String> config = ZaloPayUtil.config;

        Map<String, String> embedData = createEmbedData(orders);
        List<Map<String, Object>> items = new ArrayList<>();
        Map<String, Object> order = createOrderMap(config, orders, embedData, items);

        String data = createDataString(order);
        String mac = HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, config.get("key1"), data);
        order.put("mac", mac);
        ResponseEntity<String> response = sendRequest(config.get("endpoint"), order);

        String orderUrl = extractOrderUrl(response.getBody());
        return "redirect:" + orderUrl;
    }
}
