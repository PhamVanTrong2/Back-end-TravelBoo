package com.bootravel.payload.responses;

import com.bootravel.entity.BookingRoomsEntity;
import lombok.Data;

@Data
public class UpdateQrResponse {
    private BookingRoomsEntity bookingRoom;
    private GetBookingDetailByIdResponse detailInfo;
}
