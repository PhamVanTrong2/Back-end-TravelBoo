package com.bootravel.payload.responses;

import com.bootravel.entity.RoomAvailableByDatesEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalenderRoomAvailableResponse {
    private List<RoomAvailableByDatesEntity> roomAvailableByDatesEntity = new ArrayList<>();

    private Long numberRoom;
}
