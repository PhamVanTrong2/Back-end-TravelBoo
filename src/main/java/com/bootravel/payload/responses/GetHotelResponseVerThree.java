package com.bootravel.payload.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetHotelResponseVerThree {
    private Long id;
    private String name;
    private Integer star;
    private BigDecimal lattitude;
    private BigDecimal longtitude;
    private String description;
    private BigDecimal defaultPrice;
    private String note;
    private String taxCode;
    private boolean status;
    private Long boId;
    private String address1;
    private String address2;
    private String wardName;
    private String districtName;
    private String provinceName;
    private List<String> listImages;
    private boolean confirmNow;
    private boolean includesBreakfast;
    private String bookedAgo;
    private List<String> listService;
    private String boName;
    private PromotionDefaultResponse promotion;
    private BigDecimal finalPrice;
}
