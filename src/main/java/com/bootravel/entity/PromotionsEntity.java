package com.bootravel.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class PromotionsEntity {
    private Long id;
    private String code;
    private String name;
    private String imageUrl;
    private String description;
    private Date startDate;
    private Date endDate;
    private Integer typePromotion;
    private Integer discountPercent;
    private BigDecimal maxDiscount;
    private BigDecimal fixMoneyDiscount;
    private Integer typeMaxUse;
    private Integer maxUse;
    private Timestamp createdDate;
    private Timestamp modifiedDate;
    private String status;
}
