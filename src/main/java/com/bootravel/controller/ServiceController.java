package com.bootravel.controller;

import com.bootravel.entity.HotelServicesEntity;
import com.bootravel.payload.responses.GetListRoomServiceResponse;
import com.bootravel.payload.responses.data.ResponseListWithMetaData;
import com.bootravel.service.ServiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/service")
public class ServiceController {
    @Autowired
    private ServiceService serviceService;

    @GetMapping("/list-service-hotel")
    public ResponseListWithMetaData<HotelServicesEntity> listServiceHotel() {
        return serviceService.getListServiceHotel();
    }

    @GetMapping("/list-service-room")
    public ResponseListWithMetaData<GetListRoomServiceResponse> listServiceRoom() {
        return serviceService.getListServiceRoom();
    }
}
