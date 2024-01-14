package com.bootravel.controller;

import com.bootravel.payload.requests.CheckRoomByDateRequest;
import com.bootravel.payload.requests.RoomRequestVerTwo;
import com.bootravel.payload.responses.RoomResponeseVerTwo;
import com.bootravel.service.RoomService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/room-users")
@Api(tags = "Room API", description = "Endpoints for managing rooms")
public class RoomController {
    @Autowired
    private RoomService roomService;

    @PostMapping("/get-room-details")
    public RoomResponeseVerTwo getRoomDetails(@RequestBody RoomRequestVerTwo request) throws Exception {
        return roomService.getRoomDetails(request);
    }


    @PostMapping("/get-room/hotelId")
    public List<RoomResponeseVerTwo> getRoomByHotelId(@RequestBody RoomRequestVerTwo request) throws Exception {
        return roomService.getRoomByHotelId(request);
    }

    @GetMapping("/get-service-room")
    public  List<String> getServiceRoomById(@RequestParam long roomId ){
        return roomService.getService(roomId);
    }

    @PostMapping("/get/check-room")
    public  List<RoomResponeseVerTwo> checkRoom(@RequestBody CheckRoomByDateRequest roomId ) throws Exception {
        return roomService.searchRoomUser(roomId);
    }


}

