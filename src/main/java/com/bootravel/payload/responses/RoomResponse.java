package com.bootravel.payload.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomResponse {
    private Long id;
    private String name;
    private String roomCode;
    private String roomType;
    private Integer roomCount;
    private Integer roomSize;
    private Integer maxPeopleStay;
    private BigDecimal defaultPrice;
    private Boolean status;

}
