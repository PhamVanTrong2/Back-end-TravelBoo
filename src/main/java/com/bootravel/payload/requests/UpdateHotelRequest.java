package com.bootravel.payload.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateHotelRequest {

    private Long id;
    private String name;
    private Integer star;
    private String description;
    private String note;
    private Long businessOwner;
    private String taxCode;
    private List<Long> listHotelService;
    private long wardId;
    private String address1;
    private String address2;
    private boolean confirmNow;
    private boolean includesBreakfast;
    private String phoneNumber;
}
