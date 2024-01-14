package com.bootravel.payload.responses;

import com.bootravel.entity.RoomServicesEntity;
import lombok.Data;

import java.util.List;

@Data
public class GetListRoomServiceResponse {
    private Long roomServiceTypeId;
    private String roomServiceTypeName;
    private List<RoomServicesEntity> listRoomService;
}
