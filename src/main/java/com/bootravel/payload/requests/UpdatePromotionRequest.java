package com.bootravel.payload.requests;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class UpdatePromotionRequest {
    private Long id;
    private String name;
    private String description;
    private Date startDate;
    private Date endDate;
    private Integer typePromotion;
    private Integer discountPercent;
    private BigDecimal maxDiscount;
    private BigDecimal fixMoneyDiscount;
    private Integer typeMaxUse;
    private Integer maxUse;
}
