package com.bootravel.payload.responses;

import com.bootravel.entity.Price;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomResponeseVerTwo {
    private Long id;
    private String name;
    private Integer roomCount;
    private Integer roomSize;
    private String roomTypeName;
    private BigDecimal minPrice;
    private BigDecimal defaultPrice;
    private BigDecimal priceBydate;
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
    private List<BedRoomInfo> bedsRooms;
    private List<String> listImage;
    @Getter
    private Map<String, BigDecimal> priceByPerDate;
    private Long standardNumberOfPeople;
    private Long roomTypeId;

    private String roomCode;

    private List<String> listService;

    private Map<String, BigDecimal> priceByDateString;
    private PromotionDefaultResponse promotion;

    public void setPriceByPerDate(Map<String, BigDecimal> priceByPerDate) {
        this.priceByPerDate = priceByPerDate;
    }
}
