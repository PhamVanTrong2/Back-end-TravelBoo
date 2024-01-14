package com.bootravel.payload.responses.commonResponses;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

public class EmailBookingResponses {
    private Long id;
    private Integer userId;
    private String email;
    private String phoneNumber;
    private Date checkin;
    private Date checkout;
    private BigDecimal totalPrice;
    private String note;
    private Integer status;
    private LocalDateTime timeBooking;
    private Integer promotionId;
    private String lastName;
    private String firstName;
    private Long roomCode;
}
