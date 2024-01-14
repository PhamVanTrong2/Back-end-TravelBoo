package com.bootravel.payload.responses;

import lombok.Data;

import java.util.List;

@Data
public class TotalBookingWeeklyResponse {
    private List<BookingWeekly> listBookingWeekly;
    @Data
    public static class BookingWeekly {
        private int day;
        private int numberBooking;
    }
}
