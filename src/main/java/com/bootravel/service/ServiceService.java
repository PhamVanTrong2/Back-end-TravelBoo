package com.bootravel.service;

import com.bootravel.entity.HotelServicesEntity;
import com.bootravel.payload.responses.GetListRoomServiceResponse;
import com.bootravel.payload.responses.data.ResponseListWithMetaData;
import com.bootravel.repository.ServiceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    public ResponseListWithMetaData<HotelServicesEntity> getListServiceHotel() {
        List<HotelServicesEntity> hotelServices = serviceRepository.getListServiceHotel();
        ResponseListWithMetaData<HotelServicesEntity> response = new ResponseListWithMetaData<>();
        response.setData(hotelServices);
        response.setCode("200");
        return response;
    }

    public ResponseListWithMetaData<GetListRoomServiceResponse> getListServiceRoom() {
        ResponseListWithMetaData<GetListRoomServiceResponse> response = new ResponseListWithMetaData<>();
        List<GetListRoomServiceResponse> data = serviceRepository.getListServiceRoom();
        response.setData(data);
        response.setCode("200");
        return response;
    }
}
