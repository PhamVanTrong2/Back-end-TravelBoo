package com.bootravel.payload.responses;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DataNumberBusinessAdminResponse {
    private Integer totalHotel;
    private Integer totalStaff;
    private BigDecimal totalRevenue;
    private BigDecimal totalProfit;
    private Integer totalBooking;
}
