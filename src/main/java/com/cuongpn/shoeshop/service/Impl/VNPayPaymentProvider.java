package com.cuongpn.shoeshop.service.Impl;

import com.cuongpn.shoeshop.entity.Order;
import com.cuongpn.shoeshop.service.PaymentProvider;
import com.cuongpn.shoeshop.util.VNPAYUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service(VNPayPaymentProvider.BEAN_ID)
public class VNPayPaymentProvider implements PaymentProvider {
    public static final String BEAN_ID = "vNPayPaymentProvider";


    @Override
    public String createPayment(Order order) {

        Map<String, String> vnp_Params = createVnpParams(order);

        String queryUrl = buildQueryAndHashData(vnp_Params);
        String salt = VNPAYUtil.vnp_HashSecret;
        String vnp_SecureHash = VNPAYUtil.hmacSHA512(salt, queryUrl);

        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VNPAYUtil.vnp_PayUrl + "?" + queryUrl;
        return "redirect:" + paymentUrl;


    }

    private Map<String, String> createVnpParams(Order order) {
        String vnpVersion = "2.1.0";
        String vnpCommand = "pay";
        String vnpOrderInfo = "Thanh toan cho order " + order.getId() + ". So tien " + order.getTotal().toString();
        String orderType = "200000";
        String vnpTxnRef = order.getId().toString();
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String vnpIpAddr = VNPAYUtil.getClientIp(request);
        String vnpTmnCode = VNPAYUtil.vnp_TmnCode;
        Long amount = order.getTotal().multiply(new BigDecimal(100)).longValue();

        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", vnpVersion);
        vnpParams.put("vnp_Command", vnpCommand);
        vnpParams.put("vnp_TmnCode", vnpTmnCode);
        vnpParams.put("vnp_Amount", String.valueOf(amount));
        vnpParams.put("vnp_CreateDate", getCurrentDate());
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_IpAddr", vnpIpAddr);
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_OrderInfo", vnpOrderInfo);
        vnpParams.put("vnp_OrderType", orderType);
        vnpParams.put("vnp_ReturnUrl", VNPAYUtil.vnp_Returnurl);
        vnpParams.put("vnp_ExpireDate", getExpireDate());
        vnpParams.put("vnp_TxnRef", vnpTxnRef);

        return vnpParams;
    }

    private String getCurrentDate() {
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        return formatter.format(cld.getTime());
    }

    private String getExpireDate() {
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        cld.add(Calendar.MINUTE, 15);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        return formatter.format(cld.getTime());
    }

    private String buildQueryAndHashData(Map<String, String> vnpParams) {
        return vnpParams.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry ->
                        URLEncoder.encode(entry.getKey(), StandardCharsets.US_ASCII) + "=" +
                                URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII)
                )
                .collect(Collectors.joining("&"));
    }


}
