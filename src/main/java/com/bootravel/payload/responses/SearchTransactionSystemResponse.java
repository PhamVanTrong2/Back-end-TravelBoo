package com.bootravel.payload.responses;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class SearchTransactionSystemResponse {
    private Long id;
    private String customerName;
    private String hotelName;
    private String paymentMethod;
    private BigDecimal amount;
    private BigDecimal amountSystemReceiver;
    private Timestamp transactionTime;
    private String status;
}
