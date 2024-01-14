package com.bootravel.payload.requests;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class BookingRoomRequest {

    @NotNull
    private String email;

    @NotNull
    private String phoneNumber;

    @NotNull
    private Date checkin;

    @NotNull
    private Date checkout;

    private String note;

    private String promotionCode;

    private BigDecimal totalPrice;
    @NotNull
    private String lastName;

    @NotNull
    private String firstName;

    @NotNull
    private Integer totalDay;

    private BookingRoomDetailsRequest bookingRoomDetailsEntity;
    private TransactionRequest transactionsEntity;

    private boolean rentByTheWeek;

    private boolean rentByTheMonth;

    private SurCharge surcharge;


}
