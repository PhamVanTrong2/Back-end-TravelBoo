package com.bootravel.payload.responses;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class GetRoomByIdResponse {
    private RoomResponeseVerTwo responeseVerTwo;

    private List<Long> listRoomService;
}
