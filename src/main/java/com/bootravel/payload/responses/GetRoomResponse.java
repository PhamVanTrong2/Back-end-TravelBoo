package com.bootravel.payload.responses;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
@Data
@Builder
public class GetRoomResponse {
    private Long roomId;
    private String roomName;
    private String roomCode;
    private Integer roomCount;
    private Integer roomSize;
    private String roomTypeName;
    private Integer maxPeopleStay;
    private Integer standardNumberOfPeople;
    private Boolean confirmNow;
    private Boolean confirmationWithinMinute;
    private Boolean priceIncludesBreakfast;
    private Boolean surchargeForAdultChild;
    private BigDecimal minPrice;
    private BigDecimal defaultPrice;
    private BigDecimal weekPrice;
    private BigDecimal monthPrice;
    private BigDecimal additionalAdultFee;
    private BigDecimal additionalChildFee;
    private List<BedRoomInfo> bedsRooms;
    private List<String> listService;
    private List<String> listImage;

}
