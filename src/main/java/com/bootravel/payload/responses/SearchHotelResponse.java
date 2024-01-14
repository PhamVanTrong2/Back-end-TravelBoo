package com.bootravel.payload.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchHotelResponse {

    private Long id;
    private String name;
    private BigDecimal star;
    private String address;
    private String taxCode;
    private boolean status;
    private String businessOwnerName;
}
