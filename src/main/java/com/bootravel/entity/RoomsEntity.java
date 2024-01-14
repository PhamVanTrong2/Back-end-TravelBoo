package com.bootravel.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import java.math.BigDecimal;
import java.util.List;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class  RoomsEntity {
    private Long id;
    private String name;
    private Integer roomCount;
    private Integer roomSize;
    private Integer roomTypeId;
    private Integer occupancy;
    private BigDecimal minPrice;
    private BigDecimal defaultPrice;
    private BigDecimal weekPrice;
    private BigDecimal monthPrice;
    private BigDecimal additionalAdultFee;
    private BigDecimal additionalChildFee;
    private Integer maxPeopleStay;
    private Boolean status;
    private Integer hotelId;
    private Boolean surchargeForAdultChild;
    private Boolean priceIncludesBreakfast;
    private Boolean comfirmationWithinMinute;
    private Boolean comfirmNow;
    private Long standardNumberOfPeople;
    private String roomCode;

    private List<String> listService;
}
