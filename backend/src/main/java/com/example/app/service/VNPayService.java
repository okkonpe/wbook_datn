package com.example.app.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VNPayService {
    @Value("${vnpay.pay-url}")
    private String vnp_PayUrl;
    @Value("${vnpay.return-url}")
    private String vnp_ReturnUrl;
    @Value("${vnpay.tmn-code}")
    private String vnp_TmnCode ;
    @Value("${vnpay.hash-secret}")
    private String secretKey;
//    @Value("${vnpay.ipn-url}")
//    private String ipn_url;
    private String vnp_Version ="2.1.0";
    private String vnp_Command="pay";
    private String orderType="other";
    @Value("${vnpay.mock.url}")
    private String mockUrl;
    private boolean useMock = false; // switch khi cần
    private static final DateTimeFormatter VNP_FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final ZoneId VNP_TZ = ZoneId.of("Asia/Ho_Chi_Minh");

    // Loại bỏ dấu + ký tự đặc biệt -> chỉ còn [A-Za-z0-9 ] và co gọn khoảng trắng


    public String createVNPayPaymentUrl(long amountVnd, String orderInfo ,Integer orderID, Integer khid, HttpServletRequest request) {
        if (useMock) {
            return mockUrl + "?orderId=" + orderInfo + "&amount=" + amountVnd+"&khid="+khid;
        }
        String bankCode = request.getParameter("bankCode");
        Map<String, String> vnpParamsMap = getVNPayConfig();
        vnpParamsMap.put("vnp_Amount", String.valueOf(amountVnd*100));
        vnpParamsMap.put("vnp_TxnRef", String.valueOf(orderID));
        vnpParamsMap.put("vnp_OrderInfo", "Thanh toan don hang:" + String.valueOf(orderID));

        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }
        vnpParamsMap.put("vnp_IpAddr", getIpAddress(request));
        //build query url
        String queryUrl = getPaymentURL(vnpParamsMap, true);
        String hashData = getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = hmacSHA512(secretKey, hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnp_PayUrl + "?" + queryUrl;
        return paymentUrl;
    }



    public Map<String, String> getVNPayConfig() {
        Map<String, String> vnpParamsMap = new HashMap<>();
        vnpParamsMap.put("vnp_Version", this.vnp_Version);
        vnpParamsMap.put("vnp_Command", this.vnp_Command);
        vnpParamsMap.put("vnp_TmnCode", this.vnp_TmnCode);
        vnpParamsMap.put("vnp_CurrCode", "VND");
        vnpParamsMap.put("vnp_OrderType", this.orderType);
        vnpParamsMap.put("vnp_Locale", "vn");
        vnpParamsMap.put("vnp_ReturnUrl", this.vnp_ReturnUrl);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnpCreateDate = formatter.format(calendar.getTime());
        vnpParamsMap.put("vnp_CreateDate", vnpCreateDate);
        calendar.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(calendar.getTime());
        vnpParamsMap.put("vnp_ExpireDate", vnp_ExpireDate);
        return vnpParamsMap;
    }

    public static String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();

        } catch (Exception ex) {
            return "";
        }
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ipAdress;
        try {
            ipAdress = request.getHeader("X-FORWARDED-FOR");
            if (ipAdress == null) {
                ipAdress = request.getRemoteAddr();
            }
        } catch (Exception e) {
            ipAdress = "Invalid IP:" + e.getMessage();
        }
        return ipAdress;
    }


    public static String getPaymentURL(Map<String, String> paramsMap, boolean encodeKey) {
        return paramsMap.entrySet().stream()
                .filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty())
                .sorted(Map.Entry.comparingByKey())
                .map(entry ->
                        (encodeKey ? URLEncoder.encode(entry.getKey(),
                                StandardCharsets.US_ASCII)
                                : entry.getKey()) + "=" +
                                URLEncoder.encode(entry.getValue()
                                        , StandardCharsets.US_ASCII))
                .collect(Collectors.joining("&"));
    }


}

