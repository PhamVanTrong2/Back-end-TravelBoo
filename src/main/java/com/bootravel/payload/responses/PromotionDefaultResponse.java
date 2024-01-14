package com.bootravel.payload.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromotionDefaultResponse {
    private Integer typePromotion;
    private Integer discountPercent;
    private String promotionCode;
    private BigDecimal maxDiscount;
}
