package com.bootravel.service;

import com.bootravel.common.constant.*;
import com.bootravel.common.dto.BaseSearchPagingDTO;
import com.bootravel.common.dto.PageMetaDTO;
import com.bootravel.common.security.jwt.dto.CustomUserDetails;
import com.bootravel.common.security.jwt.service.RefreshTokenService;
import com.bootravel.entity.*;
import com.bootravel.exception.BadRequestAlertException;
import com.bootravel.payload.requests.*;
import com.bootravel.payload.responses.*;
import com.bootravel.payload.responses.commonResponses.BookingTransactionResponse;
import com.bootravel.payload.responses.data.ResponseData;
import com.bootravel.payload.responses.data.ResponseListWithMetaData;
import com.bootravel.payload.responses.data.ResultResponse;
import com.bootravel.repository.*;
import com.bootravel.service.common.CommonService;
import com.bootravel.service.common.VnPayService;
import com.bootravel.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Slf4j
public class BookingService {


    @Autowired
    private CustomCommonRepository custormCommonRepository;
    private static final String ENTITY_NAME = "BookingService";

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommonService commonService;

    @Autowired
    private final PromotionService promotionService;

    @Autowired
    private RoomService roomService;

    private final TransactionsRepository transactionsRepository;

    private final RoomsRepository roomsRepository;

    private final VnPayService vnPayService;

    private static final String DEFAULT_SORT = "id";

    private static final List<String> HEADER_SORT = Arrays.asList("time_booking");

    public BookingService(CustomCommonRepository custormCommonRepository, PromotionService promotionService, TransactionsRepository transactionsRepository, RoomsRepository roomsRepository, VnPayService vnPayService, HotelRepository hotelRepository) {
        this.custormCommonRepository = custormCommonRepository;
        this.promotionService = promotionService;
        this.transactionsRepository = transactionsRepository;
        this.roomsRepository = roomsRepository;
        this.vnPayService = vnPayService;
    }


    public BookingRoomsEntity getBookingById(long id ){
        return bookingRepository.getBookingRoomsById(id);
    }

    public ResponseEntity<?> createBooking(BookingRoomRequest request, HttpServletRequest requests) throws Exception {

        LocalDate currentDate = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));

        // Convert check-in date to LocalDate
        Instant instant = request.getCheckin().toInstant();
        LocalDate checkinDate = instant.atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDate();

        // Check if check-in is in the past
        if (checkinDate.isBefore(currentDate)) {
            throw new BadRequestAlertException("Check-in date must be today or a future date", ENTITY_NAME, "DATE_INVALID");
        }


        if (request.getCheckout().before(request.getCheckin())) {
            throw new BadRequestAlertException("Check out can't before check in ", ENTITY_NAME, "DATE_INVALID");
        }
        if (!commonService.isValidEmail(request.getEmail()) && commonService.isValidPhoneNumber(request.getPhoneNumber())) {
            throw new BadRequestAlertException("Phone or email invalid ! ", ENTITY_NAME, "INVALID");
        }

        //Check booking exist
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDateIn = outputDateFormat.format(request.getCheckin());
        String formattedDateOut = outputDateFormat.format(request.getCheckout());

        //CHECK BOOKING Room exist
        var bookingExisted =bookingRepository.checkBookingExist(formattedDateIn, formattedDateOut ,request.getBookingRoomDetailsEntity().getRoomId(),request.getBookingRoomDetailsEntity().getNumberRoomBooking());
        if(bookingExisted != null &&  (Objects.equals(bookingExisted.getStatus(), BookingRoomStatusConstants.PENDING) || Objects.equals(bookingExisted.getStatus(), BookingRoomStatusConstants.IN_PROGRESS))){
            throw new BadRequestAlertException("Your booking room in required is exist or number room is not enough!", ENTITY_NAME, "EXIST");
        }

        //check room is valid
        CheckRoomByDateRequest roomRequest = new CheckRoomByDateRequest();
        roomRequest.setNumberRoom(request.getBookingRoomDetailsEntity().getNumberRoomBooking());

        // Convert java.util.Date to java.sql.Date
        java.util.Date checkinDates = request.getCheckin();
        java.sql.Date sqlCheckinDate = new java.sql.Date(checkinDates.getTime());
        roomRequest.setUserStartDateApply(sqlCheckinDate);

        java.util.Date checkoutDate = request.getCheckout();
        java.sql.Date sqlCheckoutDate = new java.sql.Date(checkoutDate.getTime());
        roomRequest.setUserEndDateApply(sqlCheckoutDate);

        roomRequest.setUserMaxPeopleStay(request.getBookingRoomDetailsEntity().getNumberGuest());


        var checkBookingDateIsValid = roomsRepository.filterRoom(roomRequest);

        if(checkBookingDateIsValid !=null){
            //Create booking
            BookingRoomsEntity bookingRooms = new BookingRoomsEntity();

            bookingRooms.setTotalPrice(null);
            bookingRooms.setNote(request.getNote());
            bookingRooms.setCheckin(request.getCheckin());
            bookingRooms.setCheckout(request.getCheckout());

            //get promotionId from code
            if(request.getPromotionCode() != null){
                var promotionId = promotionService.getPromotionByCode(request.getPromotionCode());
                bookingRooms.setPromotionId(Math.toIntExact(promotionId.getData().getId()));
            }else {
                bookingRooms.setPromotionId(null); }


            bookingRooms.setFirstName(request.getFirstName());
            bookingRooms.setLastName(request.getLastName());
            bookingRooms.setPhoneNumber(request.getPhoneNumber());
            bookingRooms.setEmail(request.getEmail());
            bookingRooms.setStatus(BookingRoomStatusConstants.PENDING);
            bookingRooms.setTimeBooking(LocalDateTime.now());
            bookingRooms.setLastModifyBy(null);
            bookingRooms.setLastModifyDate(LocalDateTime.now());

            Optional<Long> currentUserLogin = RefreshTokenService.getCurrentUserLogin();
            if(!currentUserLogin.isPresent()){
                bookingRooms.setUserId(null);
            }else{
                Long createdBy = Long.valueOf(currentUserLogin.map(Math::toIntExact).orElse(null));
                bookingRooms.setUserId(Math.toIntExact(createdBy));
            }
            long bookingId = custormCommonRepository.getSeqBookingRoomId();
            bookingRooms.setId(bookingId);
            BookingRoomsEntity booking = bookingRepository.insertBookingRoom(bookingRooms);


            //----insert bookingRoomsDetails ----//


            BookingRoomDetailsEntity bookingRoomsDetails = new BookingRoomDetailsEntity();
            // id
            if(request.isRentByTheMonth() || request.isRentByTheWeek()) {
                bookingRoomsDetails.setId(custormCommonRepository.getSeqBookingRoomDetailsId());
            }

            bookingRoomsDetails.setBookingRoomId(Math.toIntExact(booking.getId()));
            bookingRoomsDetails.setNumberRoomBooking(request.getBookingRoomDetailsEntity().getNumberRoomBooking());
            bookingRoomsDetails.setRoomId(request.getBookingRoomDetailsEntity().getRoomId());

            // get default price by room id and date , month , year

            var room = roomsRepository.getRoomById(request.getBookingRoomDetailsEntity().getRoomId());


            bookingRoomsDetails.setPrice(request.getTotalPrice());

            bookingRoomsDetails.setNumberGuest(request.getBookingRoomDetailsEntity().getNumberGuest());
            Map<Long, Price> mapPriceCalender = new HashMap<>();
            if (Objects.nonNull(room)) {
                mapPriceCalender = roomsRepository.searchListRoomPriceCalender(request.getCheckin(),
                        request.getCheckout(), new ArrayList<>(Arrays.asList(room)));

            }
            Map<String, BigDecimal> mapPriceByDate = roomService.calculatePrice(request.getCheckin(),
                    request.getCheckout(), mapPriceCalender.get(room.getId()).getMonthPrice(),
                    mapPriceCalender.get(room.getId()).getWeekPrice(), mapPriceCalender.get(room.getId()).getMapPriceByDate());
            bookingRepository.createBookingRoomDetail(request, bookingRoomsDetails, mapPriceByDate);


            //? To Do logic update TotalPrice of booking with promotion

            if (request.getPromotionCode() != null) {

                //----insert transaction ----//


                var totalUpdated = updateTotal(booking,request.getTotalPrice()); //update totalPrice in booking room
                TransactionsEntity transactions = new TransactionsEntity();

                long transactionsId = custormCommonRepository.getSeqTransactionsId();
                transactions.setId(transactionsId);

                transactions.setBookingRoomId(Math.toIntExact(booking.getId()));
                transactions.setAmount(totalUpdated.getTotalPrice());

                //? To Do logic payment

                //Pay with cash
                if (request.getTransactionsEntity().getPaymentMethodId() == 2) { //! Sửa lại trước khi push = 2(prod) // 4(local)
                    transactions.setStatus(TransactionConstants.PENDING);  //Pending
                }
                //Pay with credit card
                if (request.getTransactionsEntity().getPaymentMethodId() == 1) { //! = 1(prod)  // 3(local)
                    transactions.setStatus(TransactionConstants.PENDING);  //DONE
                }

                //Check current user

                if(!currentUserLogin.isPresent()){
                    transactions.setUserId(null);
                }else{
                    Integer createdBy = currentUserLogin.map(Math::toIntExact).orElse(null);
                    transactions.setUserId(Math.toIntExact(createdBy));
                }


                transactions.setPaymentMethodId(request.getTransactionsEntity().getPaymentMethodId());
                var insertTransactions = transactionsRepository.insertTransactions(transactions);

                //----insert redemption promotions----//

                PromotionRedemptionsEntity redemptions = new PromotionRedemptionsEntity();

                long redemptionsId = custormCommonRepository.getSeqPromotionsRedemptionId();
                redemptions.setId(redemptionsId);

                redemptions.setPromotionId(booking.getPromotionId());
                redemptions.setUserId(booking.getUserId());
                redemptions.setRedemptionDate(Timestamp.from(Instant.now()));

                // Number money discounted
                BigDecimal totalAmountDiscounted = request.getTotalPrice().subtract(totalUpdated.getTotalPrice());

                redemptions.setRedeemedAmount(totalAmountDiscounted);
                redemptions.setTransactionId(Math.toIntExact(insertTransactions.getId()));
                promotionService.insertPromotionRedemp(redemptions);

                BigDecimal decimalValue = new BigDecimal(String.valueOf(totalUpdated.getTotalPrice()));
                long longValue = decimalValue.longValue();

                var payment = vnPayService.createPayment(longValue,transactions.getId().toString(),booking.getId().toString());

                BookingTransactionResponse response = new BookingTransactionResponse();
                response.setBookingId(booking.getId());
                response.setTransactionId(insertTransactions.getId());
                response.setVnPayResponse(payment);

                return ResponseEntity.ok().body(response);
            }
             // * NOT HAVE PROMOTIONS
            var updateBooking =bookingRepository.updateTotalPrice(booking.getId(), request.getTotalPrice());


            TransactionsEntity transactionsNotPro = new TransactionsEntity();

            long transactionsId = custormCommonRepository.getSeqTransactionsId();
            transactionsNotPro.setId(transactionsId);

            transactionsNotPro.setBookingRoomId(Math.toIntExact(updateBooking.getId()));
            transactionsNotPro.setAmount(request.getTotalPrice());
            transactionsNotPro.setPaymentMethodId(request.getTransactionsEntity().getPaymentMethodId());


            //? To Do logic payment

            //Pay with cash
            if (request.getTransactionsEntity().getPaymentMethodId() == 2) { //! Sửa lại trước khi push = 2 // 4
                transactionsNotPro.setStatus(TransactionConstants.PENDING);
            }
            //Pay with credit card
            if (request.getTransactionsEntity().getPaymentMethodId() == 1) { //! = 1 // 3
                transactionsNotPro.setStatus(TransactionConstants.PENDING);
            }

            //Check current user

            if(!currentUserLogin.isPresent()){
                transactionsNotPro.setUserId(null);
            }else{
                Integer createdBy = currentUserLogin.map(Math::toIntExact).orElse(null);
                transactionsNotPro.setUserId(Math.toIntExact(createdBy));
            }

            var  insertTransactionsId = transactionsRepository.insertTransactions(transactionsNotPro);

            BigDecimal decimalValue = new BigDecimal(String.valueOf(request.getTotalPrice()));
            long longValue = decimalValue.longValue();

            var returnVnPay = vnPayService.createPayment(Long.parseLong(String.valueOf(longValue)),insertTransactionsId.getId().toString(),booking.getId().toString());


            BookingTransactionResponse response = new BookingTransactionResponse();
            response.setBookingId(booking.getId());
            response.setTransactionId(insertTransactionsId.getId());
            response.setVnPayResponse(returnVnPay);

            return ResponseEntity.ok().body(response);
        }
       throw new BadRequestAlertException("The date you booked does not exist",ENTITY_NAME,"DATE_INVALID");
    }

    //update totalPrice in booking room
    public BookingRoomsEntity updateTotal(BookingRoomsEntity booking , BigDecimal total){
            BigDecimal totals = null;
            BigDecimal totalDiscounted;
            var promotions = promotionService.getPromotionById(Long.valueOf(booking.getPromotionId()));

            if((promotions.getData().getStatus().equals((StatusPromotionConstants.ACTIVE).toString()))){
                boolean checkPromotion =  checkPromotionIsValid(promotions.getData());

                if(checkPromotion){
                    // Giảm theo phần trăm
                    if(Objects.equals(promotions.getData().getTypePromotion(), PromotionTypeConstants.PERCENT_REDUCTION)){
                        // số tiền được giảm
                        totals = total
                                .multiply(new BigDecimal(promotions.getData().getDiscountPercent()).divide(new BigDecimal(100)));
                        totalDiscounted = total.subtract(totals);
                        // số tiền đc giảm nhỏ hơn giới hạn số tiền giảm đã quy định thì thực hiện
                        if (totals.compareTo(promotions.getData().getMaxDiscount()) < 0) {

                            return bookingRepository.updateTotalPrice(booking.getId(),totalDiscounted);
                        }else {
                            return bookingRepository.updateTotalPrice(booking.getId(), total.subtract(promotions.getData().getMaxDiscount()));
                        }
                    }
                    // Giảm theo tiền cố định
                    totals = total.subtract(promotions.getData().getFixMoneyDiscount());


                    return bookingRepository.updateTotalPrice(booking.getId(), totals);
                }
                throw new BadRequestAlertException("Your promotion out of usage",ENTITY_NAME,"PROMOTION_INVALID");

            }
            throw new BadRequestAlertException("Your promotion is expired  or not ready to use",ENTITY_NAME,"PROMOTION_INVALID");
    }
    public boolean checkPromotionIsValid(PromotionsEntity entity){
        var promotions = promotionService.getPromotionById(entity.getId());

        Optional<Long> currentUserLogin = RefreshTokenService.getCurrentUserLogin();
        Integer createdBy = currentUserLogin.map(Math::toIntExact).orElse(null);


        // false là giới hạn và ngược lại
        if(promotions.getData().getTypeMaxUse().equals(TypeMaxUsePromotion.UNLIMITED) ){
            return true; // không giới hạn
        }
        else {
            var check = promotionService.checkUserUsedPromotion(createdBy,promotions.getData().getId());
            if(check){
                return false;
            }else
            if ((promotions.getData().getMaxUse() - 1) < 0) {
                throw new BadRequestAlertException("This promotion code has been used up", ENTITY_NAME, "Invalid");
            }
            promotionService.updateMaxUse(promotions.getData().getId(), promotions.getData().getMaxUse() - 1);
            return promotions.getData().getMaxUse().compareTo(0) > 0;
        }
    }

    public BookingRoomsEntity updateStatusBookingRoom(long id, Integer newStatus){
        return bookingRepository.updateStatusBookingRoom(id,newStatus);
    }

    public ResponseListWithMetaData<SearchBookingResponse> searchBooking(SearchBookingRequest request) {
        if (Objects.isNull(request)) {
            request = new SearchBookingRequest();
        }

        CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ResponseListWithMetaData<SearchBookingResponse> responseListData = new ResponseListWithMetaData<>();
        Integer totalRecords = bookingRepository.getTotalBookingOutput(request, principal.getId());

        BaseSearchPagingDTO pagingDTO = request.getSearchPaging();
        PageMetaDTO meta = commonService.settingPageMetaInfo(request.getSearchPaging(),
                StringUtils.isEmpty(pagingDTO.getSortBy()) ? HEADER_SORT : Collections.singletonList(pagingDTO.getSortBy()),
                DEFAULT_SORT, totalRecords);

        List<SearchBookingResponse> listData = bookingRepository.searchBooking(request, principal.getId());
        responseListData.setSuccessResponse(meta, listData);
        return responseListData;
    }

    public ResponseData<GetBookingDetailByIdResponse> getBookingDetailById(Long id) {
        if (id == null) return null;
        CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        GetBookingDetailByIdResponse data = bookingRepository.getBookingDetailById(principal.getId(), id);
        ResponseData<GetBookingDetailByIdResponse> responseData = new ResponseData<>();
        if(data == null) {
            responseData.setCode(404);
        } else {
            responseData.setData(data);
        }
        return responseData;
    }

    public ResponseData<HistoryBookingResponse> historyBooking(HistoryBookingRequest request) {
        CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        HistoryBookingResponse data = bookingRepository.historyBooking(principal.getId(), request);

        ResponseData<HistoryBookingResponse> response = new ResponseData<>();
        response.setData(data);
        return response;
    }


    public CalculateDate calculateDayStay(LocalDate startDate, LocalDate endDate) {
        CalculateDate calculateDate = new CalculateDate();
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        Period period = Period.between(startDate, endDate);
        int months = period.getMonths();
        if (months >= 1 ) {
            calculateDate.setMonth(period.getMonths());
        }
        if (period.getDays() >= 7){
            long weeks = daysBetween / 7;
            long remainingDays = daysBetween % 7;
            calculateDate.setWeek(weeks);
            calculateDate.setDay(remainingDays);
        }
        return calculateDate;
    }
    @Scheduled(cron = "0 0 12 * * ?", zone = "Asia/Ho_Chi_Minh") // Run at 12:00 PM (noon) every day in Asia/Ho_Chi_Minh timezone
    @Async
    public void autoCheckout() {
        List<BookingRoomsEntity> bookings = bookingRepository.getAllBookingRooms();
        Date currentDate = new Date(); // Get the current date

        for (BookingRoomsEntity booking : bookings) {
            Date checkoutDate = parseDate(String.valueOf(booking.getCheckout())); // Assuming getCheckOut() returns a String date
            if (currentDate.after(checkoutDate) && booking.getStatus().equals(BookingRoomStatusConstants.IN_PROGRESS)) {
                // Perform the update
                updateStatusBookingRoom(booking.getId(), BookingRoomStatusConstants.DONE);
            }
            LocalDateTime currentDateTime = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).plusMinutes(30);
            if(booking.getTimeBooking().isAfter(currentDateTime) && booking.getStatus().equals(BookingRoomStatusConstants.PENDING)) {
                // Perform the update
                updateStatusBookingRoom(booking.getId(), BookingRoomStatusConstants.CANCEL);
            }
        }
    }

    // Method to parse a String date into a Date object
    private Date parseDate(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace(); // Handle the exception appropriately
            return null;
        }

    }

    public ResultResponse updateStatusCancelRoomBooking(Long id) throws Exception {
        ResultResponse response = new ResultResponse();
        if (id == null ) {
            response.setCode(400);
            response.setMessage("Id not exist");
            return response;
        }
        BookingRoomsEntity entity = updateStatusBookingRoom(id, BookingRoomStatusConstants.CANCEL_BY_HOTEL);
        if(entity == null) {
            response.setCode(400);
            response.setMessage("Id not exist");
            return response;
        }
        Date checkin = entity.getCheckin();
        Date checkout = entity.getCheckout();

        BookingRoomDetailsEntity detailsEntity = bookingRepository.getBookingRoomDetailsByBookingRoomId(id);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(checkin);
        while (calendar.getTime().compareTo(checkout) < 0) {
            Date currentDate = calendar.getTime();
            Integer numberRoomAvailable = roomsRepository.getNumberRoomAvailable(detailsEntity.getRoomId(), currentDate);
            roomsRepository.updateRoomAvailableByRoomId(numberRoomAvailable + detailsEntity.getNumberRoomBooking(), detailsEntity.getRoomId(), currentDate);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return response;
    }

    public BookingRoomsEntity getBookingQrById(Long id) {
        if (id == null) return null;
        CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return bookingRepository.getBookingQrById(principal.getId(), id);
    }
}
