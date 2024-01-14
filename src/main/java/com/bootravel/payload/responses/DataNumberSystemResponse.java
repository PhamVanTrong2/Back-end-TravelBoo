package com.bootravel.payload.responses;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DataNumberSystemResponse {
    private Integer totalHotel;
    private Integer totalUser;
    private BigDecimal totalRevenue;
    private BigDecimal totalProfit;
    private Integer totalBooking;
    private Integer totalPromotion;
}
