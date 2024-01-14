package com.bootravel.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class HotelsEntity {
    private Long id;
    private String name;
    private BigDecimal star;
    private BigDecimal lattitude;
    private BigDecimal longtitude;
    private String description;
    private String note;
    private Long addressId;
    private Long bankId;
    private String taxCode;
    private boolean confirmNow;
    private boolean includesBreakfast;
    private Date bookedAgo;
    private String hotelPhoneNumber;
}
