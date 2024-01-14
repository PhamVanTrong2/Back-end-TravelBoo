package com.bootravel.controller;

import com.bootravel.payload.requests.commonRequests.HotelFilterRequest;
import com.bootravel.payload.responses.SearchHotelGuestReponse;
import com.bootravel.payload.responses.SuggestHotelResponse;
import com.bootravel.payload.responses.SuggestLocationResponse;
import com.bootravel.payload.responses.data.ResponseData;
import com.bootravel.payload.responses.data.ResponseListData;
import com.bootravel.service.HotelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.Null;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/hotel-home")
public class HotelController {
    @Autowired
    private HotelService hotelService;
    @PostMapping("/search-hotel")
    public ResponseData<SearchHotelGuestReponse> searchRoom(@RequestBody HotelFilterRequest filterParams) throws Exception {
        SearchHotelGuestReponse filteredRooms = hotelService.filterHotel(filterParams);

        // Get the total count of results
        int totalCount = filteredRooms.getListHotel().size();

        ResponseData responseData = new ResponseData();
        filteredRooms.setTotalCount(totalCount);
        responseData.setData(filteredRooms);
        return responseData;
    }
    @GetMapping("/get-service-hotel")
    public  List<String> getServiceHotelById(@RequestParam long hotelId ){
        return hotelService.getService(hotelId);
    }

    @PostMapping("/search-suggest-hotel")
    public ResponseListData<SuggestHotelResponse> searchSuggestHotel(@RequestBody @Null String province) throws Exception{
        return hotelService.searchSuggestHotels(province);
    }

    @PostMapping("/search-suggest-location")
    public ResponseListData<SuggestLocationResponse> searchSuggestLocation() throws Exception{
        return hotelService.searchSuggestLocation();
    }

}
