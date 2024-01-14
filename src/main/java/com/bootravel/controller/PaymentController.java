package com.bootravel.controller;

import com.bootravel.common.constant.BookingRoomStatusConstants;
import com.bootravel.common.constant.TransactionConstants;
import com.bootravel.exception.BadRequestAlertException;
import com.bootravel.payload.requests.commonRequests.MailsRequests;
import com.bootravel.payload.responses.RoomAvailabilityResponse;
import com.bootravel.repository.BookingRepository;
import com.bootravel.repository.RoomsRepository;
import com.bootravel.service.BookingService;
import com.bootravel.service.TransactionService;
import com.bootravel.service.common.EmailService;
import com.bootravel.service.common.VnPayService;
import com.bootravel.utils.FilesUtils;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/api/payment")
public class PaymentController {
    @Autowired
    private VnPayService vnPayService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private final EmailService emailService;

    private static final String SEND_NAME = "TravelBoo";
    private static final String TITLE = "QR CHECKIN";
    private final BookingRepository bookingRepository;
    private final RoomsRepository roomsRepository;

    private final String ENTITY_NAME = "PaymentController";
    private static final String STATUS_RESPONSES_FAIL = "error";
    private static final String BOOKING_FAIL = "warning";
    private static final String REVERSED_TRANSACTION = "Khách hàng đã bị trừ tiền tại Ngân hàng nhưng GD chưa thành công ở VNPAY";

    public PaymentController(EmailService emailService, BookingRepository bookingRepository, RoomsRepository roomsRepository) {
        this.emailService = emailService;
        this.bookingRepository = bookingRepository;
        this.roomsRepository = roomsRepository;
    }

        @PostMapping("/create")
    public String createPayment(long amount) throws UnsupportedEncodingException {
            return vnPayService.createPayment(amount,"1","1");
    }


    @GetMapping("/queryTransaction")
    public String queryTransaction(
            @RequestParam(required = false) String vnp_TxnRef,long transactionId,long bookingId,
            HttpServletRequest request) throws Exception {
        var checkBooking = bookingRepository.getBookingRoomsById(bookingId);
        var checkBookingDetails = bookingRepository.getBookingRoomDetailsByBookingRoomId(bookingId);
        // Tạo request data
        Map<String, String> requestData = vnPayService.createQueryDrRequestData(vnp_TxnRef, request);

        // Gửi yêu cầu đến API VNPAY
        ResponseEntity<String> responseEntity = vnPayService.sendQueryDrRequest(requestData);
        String responseBody = responseEntity.getBody();
        var result = vnPayService.handleQueryDrResponse(responseBody);

        if(result.equals("SUCCESS")){
           var bookingExisted = bookingRepository.checkBookingExist(checkBooking.getCheckin().toString(),checkBooking.getCheckout().toString(),checkBookingDetails.getRoomId(),checkBookingDetails.getNumberRoomBooking());
            if(bookingExisted != null &&  (Objects.equals(bookingExisted.getStatus(), BookingRoomStatusConstants.PENDING) || Objects.equals(bookingExisted.getStatus(), BookingRoomStatusConstants.IN_PROGRESS))){
                emailService.sendEmail("TEMPLATE.html",
                        checkBooking.getEmail(), "TravelBoo",null,null
                );
                transactionService.updateStatusTransaction(transactionId, TransactionConstants.SUCCESS);

                bookingService.updateStatusBookingRoom(bookingId, BookingRoomStatusConstants.REFUND);

                return BOOKING_FAIL;
            }
         //update transaction/booking status
            transactionService.updateStatusTransaction(transactionId, TransactionConstants.SUCCESS);

            bookingService.updateStatusBookingRoom(bookingId, BookingRoomStatusConstants.IN_PROGRESS);
            //----send mail ----//

            MailsRequests requestsMail = new MailsRequests();
            requestsMail.setName(SEND_NAME);
            requestsMail.setTitle(TITLE);
            var siteUrl = emailService.getSiteURL(request);
            var booking = bookingService.getBookingById(bookingId);
            var bookingDetail = bookingRepository.getBookingRoomDetailsByBookingRoomId(booking.getId());

            // Get the current checkout date
            Date checkoutMinus = booking.getCheckout();

            // Convert java.sql.Date to java.util.Date
            java.util.Date utilDate = new java.util.Date(checkoutMinus.getTime());

            // Convert the checkin date to LocalDate
            LocalDate localDate = utilDate.toInstant().atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDate();

            // Subtract one day
            LocalDate updatedLocalDate = localDate.minusDays(1);

            // Convert the updated LocalDate back to Instant
            Instant updatedInstant = updatedLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

            // Convert the updated Instant back to java.util.Date
            java.util.Date updatedDate = Date.from(updatedInstant);

            //update room availability
            // Get room availability
            List<RoomAvailabilityResponse> roomAvailabilityList = roomsRepository.getRoomAvailableByRoomId(
                    bookingDetail.getRoomId(),
                    booking.getCheckin(),
                    updatedDate
            );
            if (roomAvailabilityList.isEmpty()) {
                System.out.println("No room availability records found for the specified room and date range.");
            } else {
                for (RoomAvailabilityResponse roomAvailability : roomAvailabilityList) {
                    // Update room availability for each record
                    int updatedRoomAvailability = roomAvailability.getNumberRoomAvailable() -  bookingDetail.getNumberRoomBooking(); // Adjust as needed
                    roomsRepository.updateRoomAvailableByRoomId(
                            updatedRoomAvailability,
                            bookingDetail.getRoomId(),
                            roomAvailability.getDateApply()
                    );
                }
                System.out.println("Room availability updated for all records successfully.");
            }
            emailService.sendEmailQr(requestsMail, siteUrl, booking);
            return "success";
        }else if(result.equals("INCOMPLETE_TRANSACTION")){
            transactionService.updateStatusTransaction(transactionId, TransactionConstants.NOT_FINISH);
            bookingService.updateStatusBookingRoom(bookingId, BookingRoomStatusConstants.CANCEL);
            return STATUS_RESPONSES_FAIL;
        }else if(result.equals("REVERSED_TRANSACTION")){
            transactionService.updateStatusTransaction(transactionId, TransactionConstants.REVERSED_TRANSACTION);
            bookingService.updateStatusBookingRoom(bookingId, BookingRoomStatusConstants.CANCEL);
            return REVERSED_TRANSACTION;
        }else
        // Trả về kết  cho client
            transactionService.updateStatusTransaction(transactionId, TransactionConstants.NOT_FINISH);
            bookingService.updateStatusBookingRoom(bookingId, BookingRoomStatusConstants.CANCEL);
        return STATUS_RESPONSES_FAIL;
    }


}
