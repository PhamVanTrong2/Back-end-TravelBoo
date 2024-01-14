package com.bootravel.payload.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchBookingResponse {
    private Long id;
    private String customerName;
    private String customerEmail;
    private String customerPhoneNumber;
    private Date checkin;
    private Date checkout;
    private BigDecimal actualPrice;
    private String status;
}
