package com.bootravel.payload.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SurCharge {
    private Integer numberChild;
    private Integer numberAdult;
    private Integer additionalAdultFee;
    private Integer additionalChildFee;
    private Integer totalFee;
}
