package com.bootravel.payload.responses;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DataNumberBusinessOwnerResponse {
    private BigDecimal totalRevenue;
    private BigDecimal totalProfit;
    private Integer totalBooking;
}
