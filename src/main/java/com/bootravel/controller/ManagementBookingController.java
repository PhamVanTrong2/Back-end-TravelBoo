package com.bootravel.controller;

import com.bootravel.payload.requests.SearchBookingRequest;
import com.bootravel.payload.responses.GetBookingDetailByIdResponse;
import com.bootravel.payload.responses.SearchBookingResponse;
import com.bootravel.payload.responses.data.ResponseData;
import com.bootravel.payload.responses.data.ResponseListWithMetaData;
import com.bootravel.payload.responses.data.ResultResponse;
import com.bootravel.service.BookingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Null;

@RestController
@Slf4j
@RequestMapping("manage-booking")
public class ManagementBookingController {
    @Autowired
    private BookingService bookingService;

    @PostMapping("/search-booking")
    public ResponseListWithMetaData<SearchBookingResponse> searchBookingSystem(@RequestBody @Null SearchBookingRequest request) throws Exception {
        return bookingService.searchBooking(request);
    }

    @GetMapping("/get-booking/{id}")
    public ResponseData<GetBookingDetailByIdResponse> getTransactionById(@PathVariable Long id) {
        return bookingService.getBookingDetailById(id);
    }

    @PostMapping("/update-status-booking/{id}")
    public ResultResponse updateStatusBooking(@PathVariable Long id) throws Exception {
        return bookingService.updateStatusCancelRoomBooking(id);
    }
}
