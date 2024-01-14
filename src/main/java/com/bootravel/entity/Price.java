package com.bootravel.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
public class Price {
    private BigDecimal monthPrice;
    private BigDecimal weekPrice;
    private Map<String, BigDecimal> mapPriceByDate;
}
