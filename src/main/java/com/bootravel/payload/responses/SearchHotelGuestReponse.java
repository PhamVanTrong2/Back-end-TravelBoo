package com.bootravel.payload.responses;

import com.bootravel.payload.requests.commonRequests.HotelFilterRequest;
import lombok.Data;

import java.util.List;

@Data
public class SearchHotelGuestReponse {
    private List<GetHotelResponseVerThree> listHotel;
    private HotelFilterRequest request;
    private long totalCount;
}
