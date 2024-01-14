package com.bootravel.controller;

import com.bootravel.common.constant.BookingRoomStatusConstants;
import com.bootravel.entity.BookingRoomsEntity;
import com.bootravel.exception.BadRequestAlertException;
import com.bootravel.payload.requests.BookingRoomRequest;
import com.bootravel.payload.requests.HistoryBookingRequest;
import com.bootravel.payload.responses.GetBookingDetailByIdResponse;
import com.bootravel.payload.responses.HistoryBookingResponse;
import com.bootravel.payload.responses.UpdateQrResponse;
import com.bootravel.payload.responses.data.ResponseData;
import com.bootravel.payload.responses.data.ResponseListWithMetaData;
import com.bootravel.service.BookingService;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Objects;

@RestController
@Slf4j
@RequestMapping("/booking-room")

public class BookingRoomController {

    private final BookingService bookingService;

    private static final String ENTITY_NAME = "BookingRoomController";
    public BookingRoomController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createBookingRoom(@RequestBody BookingRoomRequest request, HttpServletRequest requests) throws Exception {
            var booking = bookingService.createBooking(request,requests);
            return ResponseEntity.ok(booking);
    }

    @PostMapping("/update/check-qr/check-in")
    public ResponseData<UpdateQrResponse> updateBookingRoom(@RequestBody BookingRoomsEntity bookingRooms) throws Exception {
        var status = bookingService.getBookingQrById(bookingRooms.getId());
        if(status == null) {
            throw new BadRequestAlertException("Can not find booking", ENTITY_NAME, "invalid booking");
        }
        // Get the current date in the "Asia/Ho_Chi_Minh" time zone
        LocalDate currentDate = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDate();

        // Convert checkin date to LocalDate in the same time zone
        LocalDate checkinDate = bookingRooms.getCheckin().toInstant().atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDate();

        if(Objects.equals(status.getStatus(), BookingRoomStatusConstants.DONE)
                || Objects.equals(status.getStatus(), BookingRoomStatusConstants.CHECK_IN)) {
            throw new BadRequestAlertException("Can not check in!", ENTITY_NAME, "invalid checkin");
        }

        // Compare the checkin date with the current date
        if (checkinDate.compareTo(currentDate) <= 0) {
            ResponseData<UpdateQrResponse> responseData = new ResponseData<>();
            // Continue with your logic if the checkin date is valid
            BookingRoomsEntity booking = bookingService.updateStatusBookingRoom(bookingRooms.getId(), BookingRoomStatusConstants.CHECK_IN);
            UpdateQrResponse response = new UpdateQrResponse();
            ResponseData<GetBookingDetailByIdResponse> detail = bookingService.getBookingDetailById(booking.getId());
            response.setBookingRoom(booking);
            response.setDetailInfo(detail.getData());
            responseData.setData(response);
            return responseData;
        }

        throw new BadRequestAlertException("Check-in date must be greater than the current date!", ENTITY_NAME, "checkin_date");

    }

    @PostMapping("/update/check-qr/check-out")
    public ResponseData<UpdateQrResponse> updateBookingRoomCheckout(@RequestBody BookingRoomsEntity bookingRooms) throws Exception {

        var status = bookingService.getBookingQrById(bookingRooms.getId());
        if(status == null) {
            throw new BadRequestAlertException("Can not find booking", ENTITY_NAME, "invalid booking");
        }

        if(status.getStatus().equals(BookingRoomStatusConstants.CHECK_IN)) {
            ResponseData<UpdateQrResponse> responseData = new ResponseData<>();
            // Continue with your logic if the checkin date is valid
            BookingRoomsEntity booking = bookingService.updateStatusBookingRoom(bookingRooms.getId(), BookingRoomStatusConstants.DONE);
            UpdateQrResponse response = new UpdateQrResponse();
            ResponseData<GetBookingDetailByIdResponse> detail = bookingService.getBookingDetailById(booking.getId());
            response.setBookingRoom(booking);
            response.setDetailInfo(detail.getData());
            responseData.setData(response);
            return responseData;
        }
        throw new BadRequestAlertException("You can only update check in status", ENTITY_NAME, "invalid_status");
    }

    @PostMapping("/history")
    public ResponseData<HistoryBookingResponse> historyBooking(@RequestBody HistoryBookingRequest request) {
        return bookingService.historyBooking(request);
    }
}
