package com.bootravel.payload.responses;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class HistoryBookingResponse {

    private List<HistoryBooking> listBookingDone;
    private List<HistoryBooking> listBookingPending;
    @Data
    public static class HistoryBooking {
        private Long bookingId;
        private Long hotelId;
        private String hotelName;
        private String roomName;
        private String address;
        private Date checkIn;
        private Date checkOut;
        private BigDecimal totalPrice;
        private Integer numberRoomBooking;
        private Integer status;
        private String hotelImage;
    }

}
