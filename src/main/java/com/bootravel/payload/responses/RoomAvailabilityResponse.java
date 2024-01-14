package com.bootravel.payload.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomAvailabilityResponse {
    private long id;
    private int numberRoomAvailable;
    private Date dateApply;
}
