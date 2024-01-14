package com.bootravel.payload.responses;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class GetTransactionByIdResponse {
    private Long id;
    private String bookerName;
    private String bookerEmail;
    private String bookerPhoneNumber;

    private String hotelName;
    private String taxCode;
    private String address;

    private String paymentMethod;
    private BigDecimal amount;
    private BigDecimal amountSystemReceiver;
    private Timestamp transactionTime;
}
