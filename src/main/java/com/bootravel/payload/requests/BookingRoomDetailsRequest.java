package com.bootravel.payload.requests;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class BookingRoomDetailsRequest {
    private Integer roomId;
    private Integer numberRoomBooking;
    private Integer numberGuest;

    private Long numberOfAdultsArising;

    private Long numberOfChildArising;
}
