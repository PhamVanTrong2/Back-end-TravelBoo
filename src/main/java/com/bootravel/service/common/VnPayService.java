package com.bootravel.service.common;

import com.bootravel.config.VnPayConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional
public class VnPayService {

    private static final Logger log = LoggerFactory.getLogger(VnPayService.class);
    public static String vnp_IpAddr = "127.0.0.1";

    public String createPayment(long amount,String tranId, String bookId) throws UnsupportedEncodingException {

        long amountTotal = amount*100;
        String vnp_TxnRef = VnPayConfig.getRandomNumber(8);

        String vnp_TmnCode = VnPayConfig.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", VnPayConfig.vnp_Version);
        vnp_Params.put("vnp_Command", VnPayConfig.vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amountTotal));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", "NCB");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_Locale", "vn");
        String returnUrl = VnPayConfig.vnp_ReturnUrl
                .replace("[tranId]", tranId)
                .replace("[bookId]", bookId);
        vnp_Params.put("vnp_OrderType", VnPayConfig.orderType);
        vnp_Params.put("vnp_ReturnUrl", returnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);


        ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
        LocalDateTime now = LocalDateTime.now(zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String vnp_CreateDate = now.format(formatter);
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        LocalDateTime expireDateTime = now.plusMinutes(15);
        String vnp_ExpireDate = expireDateTime.format(formatter);
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VnPayConfig.hmacSHA512(VnPayConfig.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VnPayConfig.vnp_PayUrl + "?" + queryUrl;


        log.debug("vnp_ExpireDate: " + vnp_ExpireDate);
        return paymentUrl;
    }
    public Map<String, String> createQueryDrRequestData(String vnp_TxnRefs, HttpServletRequest request) {

        String vnp_RequestId = VnPayConfig.getRandomNumber(8);
        String vnp_Version = "2.1.0";
        String vnp_Command = "querydr";
        String vnp_TmnCode = VnPayConfig.vnp_TmnCode;
        String vnp_TxnRef = vnp_TxnRefs;
        String vnp_OrderInfo = "Kiem tra ket qua GD OrderId:" + vnp_TxnRef;
        String vnp_IpAddr = VnPayConfig.getIpAddress(request);

        ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
        LocalDateTime now = LocalDateTime.now(zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String vnp_CreateDate = now.format(formatter);

        Map<String, String> requestData = new HashMap<>();
        requestData.put("vnp_RequestId", vnp_RequestId);
        requestData.put("vnp_Version", vnp_Version);
        requestData.put("vnp_Command", vnp_Command);
        requestData.put("vnp_TmnCode",vnp_TmnCode);
        requestData.put("vnp_TxnRef", vnp_TxnRef);
        requestData.put("vnp_TransactionDate", vnp_CreateDate);
        requestData.put("vnp_CreateDate", vnp_CreateDate);
        requestData.put("vnp_IpAddr", vnp_IpAddr);
        requestData.put("vnp_OrderInfo",vnp_OrderInfo);


        String hash_Data= String.join("|", vnp_RequestId, vnp_Version, vnp_Command, vnp_TmnCode, vnp_TxnRef,
                vnp_CreateDate, vnp_CreateDate, vnp_IpAddr, vnp_OrderInfo);
        String vnp_SecureHash = VnPayConfig.hmacSHA512(VnPayConfig.secretKey, hash_Data.toString());

        requestData.put("vnp_SecureHash", vnp_SecureHash);

        return requestData;
    }

    public ResponseEntity<String> sendQueryDrRequest(Map<String, String> requestData) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestData, headers);

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForEntity(VnPayConfig.vnp_ApiUrl, requestEntity, String.class);
    }

    public String handleQueryDrResponse(String responseBody) throws JsonProcessingException {
        // ... (phần xử lý JSON response)
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        // Lấy các giá trị từ response
        String vnp_ResponseCode = jsonNode.get("vnp_ResponseCode").asText();
        String vnp_TransactionStatus = jsonNode.get("vnp_TransactionStatus").asText();
        if(vnp_ResponseCode.equals("00")){
            String errorCode = mapVnpTransactionStatusToErrorCode(vnp_TransactionStatus);
            // Map vnp_TransactionStatus thành mã lỗi
            // In thông báo lỗi hoặc thành công tùy thuộc vào giá trị của errorCode
            return errorCode;
        }
        return "Bad request";

    }


    private String mapVnpTransactionStatusToErrorCode(String vnp_TransactionStatus) {
        switch (vnp_TransactionStatus) {
            case "00":
                return "SUCCESS";
            case "01":
                return "INCOMPLETE_TRANSACTION";
            case "02":
                return "ERROR_TRANSACTION";
            case "04":
                return "REVERSED_TRANSACTION";
            case "05":
                return "PROCESSING_TRANSACTION";
            case "06":
                return "REFUND_REQUEST_SENT";
            case "07":
                return "SUSPECTED_FRAUD";
            case "09":
                return "REFUND_REJECTED";
            default:
                return "UNKNOWN_ERROR";
        }
    }

}

