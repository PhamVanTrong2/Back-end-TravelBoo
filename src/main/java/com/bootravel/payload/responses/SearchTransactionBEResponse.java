package com.bootravel.payload.responses;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class SearchTransactionBEResponse {
    private Long id;
    private String customerName;
    private String paymentMethod;
    private BigDecimal amount;
    private BigDecimal amountBEReceiver;
    private Timestamp transactionTime;
    private String status;
}
