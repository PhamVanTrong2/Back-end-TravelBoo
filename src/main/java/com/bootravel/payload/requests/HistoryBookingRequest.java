package com.bootravel.payload.requests;

import lombok.Data;

import java.util.Date;

@Data
public class HistoryBookingRequest {
    private Date checkin;
    private Date checkout;
}
