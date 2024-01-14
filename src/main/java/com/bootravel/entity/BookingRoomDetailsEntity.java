package com.bootravel.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class BookingRoomDetailsEntity {
    private Long id;
    private Integer bookingRoomId;
    private Integer roomId;
    private Integer numberRoomBooking;
    private BigDecimal price;
    private Integer numberGuest;

    private Long numberOfAdultsArising;

    private Long numberOfChildArising;
}
