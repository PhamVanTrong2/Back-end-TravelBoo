package com.bootravel.payload.responses;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Data
public class GetBookingDetailByIdResponse {
    private Long id;
    private String customerName;
    private String customerEmail;
    private String customerPhoneNumber;
    private Date checkin;
    private Date checkout;
    private BigDecimal totalPrice;
    private BigDecimal actualPrice;
    private String note;
    private Timestamp timeBooking;
    private String promotionCode;
    private Long roomId;
    private String roomName;
    private String roomCode;
    private Integer numberRoomBooking;
    private List<String> listRoomImage;
    private List<BookingDetail> listBookingDetail;
    @Data
    public static class BookingDetail {
        private Date fromDate;
        private Date toDate;
        private BigDecimal price;
        private Integer numberGuest;
        private Integer numberRoom;
        private Integer numberOfAdults;
        private Integer numberOfChild;
    }
}
