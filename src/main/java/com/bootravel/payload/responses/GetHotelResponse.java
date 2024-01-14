package com.bootravel.payload.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetHotelResponse {
    private Long id;
    private String name;
    private Integer star;
    private BigDecimal lattitude;
    private BigDecimal longtitude;
    private String description;
    private String note;
    private String taxCode;
    private boolean status;
    private Long boId;
    private String address1;
    private String address2;
    private Long wardId;
    private Long districtId;
    private Long provinceId;
    private String boName;
    private String phoneNumber;

    private List<String> listHotelService;

    public boolean getStatus() {
        return status;
    }

}
