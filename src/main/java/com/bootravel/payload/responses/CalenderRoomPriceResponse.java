package com.bootravel.payload.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalenderRoomPriceResponse {
    private BigDecimal defaultPrice;

    Map<Date, BigDecimal> mapPriceByDate;

}
