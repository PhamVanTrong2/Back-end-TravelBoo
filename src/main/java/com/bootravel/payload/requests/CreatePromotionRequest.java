package com.bootravel.payload.requests;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CreatePromotionRequest {
    private String code;
    private String name;
    private String description;
    private Date startDate;
    private Date endDate;
    private Integer typePromotion;
    private String discountPercent;
    private String maxDiscount;
    private String fixMoneyDiscount;
    private String maxUse;
    private Integer typeMaxUse;
}
