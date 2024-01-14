package com.bootravel.payload.responses;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TotalIncomeResponse {
    private BigDecimal totalRevenueInSixMonth;
    private BigDecimal totalProfitInSixMonth;
    private List<ResponseIncome> detailIncome;
    @Data
    public static class ResponseIncome {
        private String monthCode;
        private int monthNumber;
        private BigDecimal revenue;
        private BigDecimal profit;
    }
}
