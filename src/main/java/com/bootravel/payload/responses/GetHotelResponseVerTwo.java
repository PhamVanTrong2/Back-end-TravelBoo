package com.bootravel.payload.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetHotelResponseVerTwo {
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
    private Long wardId;
    private Long districtId;
    private Long provinceId;
    private List<Long> listServiceId;

    private List<String> listServiceName;
    private String boName;
    private String phoneNumber;
}
