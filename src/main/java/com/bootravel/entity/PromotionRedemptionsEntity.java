package com.bootravel.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class PromotionRedemptionsEntity {
    private Long id;
    private Integer userId;
    private Integer promotionId;
    private Integer transactionId;
    private BigDecimal redeemedAmount;
    private Timestamp redemptionDate;
}
