package com.bootravel.payload.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateRoomRequest {
    private Long roomId;
    private String roomName;
    private Integer roomCount;
    private Integer roomSize;
    private Integer roomTypeId;
    private BigDecimal minPrice;
    private BigDecimal defaultPrice;
    private BigDecimal weekPrice;
    private BigDecimal monthPrice;
    private BigDecimal additionalAdultFee;
    private BigDecimal additionalChildFee;
    private Integer maxPeopleStay;
    private Boolean surchargeForAdultChild;
    private Boolean priceIncludesBreakfast;
    private Boolean confirmationWithinMinute;
    private Boolean confirmNow;
    private List<Long> listService;
    private Map<Long, Long> bedsRoom;
    private Long standardNumberOfPeople;
    private String roomCode;
}
