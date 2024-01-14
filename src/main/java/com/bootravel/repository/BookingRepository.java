package com.bootravel.repository;

import com.bootravel.common.CommonRepository;
import com.bootravel.common.constant.BookingRoomStatusConstants;
import com.bootravel.common.constant.MasterDataConstants;
import com.bootravel.common.constant.TransactionConstants;
import com.bootravel.entity.BookingRoomDetailsEntity;
import com.bootravel.entity.BookingRoomsEntity;
import com.bootravel.payload.requests.BookingRoomRequest;
import com.bootravel.payload.requests.HistoryBookingRequest;
import com.bootravel.payload.requests.SearchBookingRequest;
import com.bootravel.payload.responses.*;
import com.bootravel.utils.DateUtils;
import com.bootravel.utils.HandlerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Date;

@Repository
@Slf4j
public class BookingRepository  extends CommonRepository {

    private List<String> listFieldSearchBooking = Arrays.asList("br.email", "br.phone_number");

    public BookingRepository() throws ParserConfigurationException, IOException, SAXException {
        super();
    }
    @Override
    public String getFileKey() {
        return "/sql/sqlBookingRepository.xml";
    }

    public BookingRoomsEntity insertBookingRoom(BookingRoomsEntity request) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = sqlLoader.getSql("INSERT_BOOKING_ROOM");

        try {
            List<Object> params = new ArrayList<>();
            params.add(request.getId());
            params.add(request.getUserId());
            params.add(request.getEmail());
            params.add(request.getPhoneNumber());
            params.add(request.getCheckin());
            params.add(request.getCheckout());
            params.add(request.getTotalPrice());
            params.add(request.getNote());
            params.add(request.getStatus()); // Assuming status is always 0
            params.add(request.getPromotionId());
            params.add(request.getLastModifyBy()); // Assuming the lastModifyBy field is null
            params.add(request.getLastName());
            params.add(request.getFirstName());
            ps = preparedStatement(sql, params);
            ps.executeUpdate();
            return request;

        } catch (SQLException e) {
            log.error("Error inserting booking: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return null;  // Return null if the insertion was not successful
    }

    public BookingRoomsEntity getBookingRoomsById(Long id) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = sqlLoader.getSql("GET_BOOKING_ROOM_BY_ID");

        try {
            List<Object> params = new ArrayList<>();
            params.add(id);

            ps = preparedStatement(sql, params);

            rs = ps.executeQuery();

            if (rs.next()) {
                // Retrieve data from the result set and create a BookingRoomsEntity
                BookingRoomsEntity bookingRoom = new BookingRoomsEntity();
                bookingRoom.setId(rs.getLong("ID"));
                bookingRoom.setUserId(rs.getInt("USER_ID"));
                bookingRoom.setEmail(rs.getString("EMAIL"));
                bookingRoom.setPhoneNumber(rs.getString("PHONE_NUMBER"));
                bookingRoom.setCheckin(rs.getDate("CHECK_IN"));
                bookingRoom.setCheckout(rs.getDate("CHECK_OUT"));
                bookingRoom.setTotalPrice(rs.getBigDecimal("TOTAL_PRICE"));
                bookingRoom.setNote(rs.getString("NOTE"));
                bookingRoom.setStatus(rs.getInt("STATUS"));
                bookingRoom.setTimeBooking(rs.getTimestamp("TIME_BOOKING").toLocalDateTime());
                bookingRoom.setPromotionId(rs.getInt("PROMOTION_ID"));
                bookingRoom.setLastModifyBy(rs.getInt("LAST_MODIFY_BY"));
                bookingRoom.setLastModifyDate(rs.getTimestamp("LAST_MODIFY_DATE").toLocalDateTime());
                bookingRoom.setLastName(rs.getString("LAST_NAME"));
                bookingRoom.setFirstName(rs.getString("FIRST_NAME"));

                return bookingRoom;
            }
        } catch (SQLException e) {
            log.error("Error retrieving booking by ID: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return null;  // Return null if the booking room with the given ID was not found
    }

    public BookingRoomsEntity updateTotalPrice(Long id, BigDecimal total) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = sqlLoader.getSql("UPDATE_TOTAL_PRICE_BOOKING_ROOM");

        try {
            List<Object> params = new ArrayList<>();
            params.add(total);
            params.add(id);

            ps = preparedStatement(sql, params);

            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated > 0) {
                // If the update was successful, retrieve and return the updated booking room
                return getBookingRoomsById(id);
            }
        } catch (SQLException e) {
            log.error("Error updating total price for booking room: " + e.getMessage(), e);
        } finally {
            closePS(ps);
        }

        return null;  // Return null if the update was not successful
    }

    public BookingRoomsEntity checkBookingExist(String checkin, String checkout, int roomId,int numberRoom) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = sqlLoader.getSql("CHECK_BOOKING_EXIST");

        try {
            List<Object> params = new ArrayList<>();
            params.add(java.sql.Date.valueOf(checkin));  // Assuming checkin and checkout are in "yyyy-MM-dd" format
            params.add(java.sql.Date.valueOf(checkout));
            params.add(roomId);
            params.add(roomId);
            params.add(java.sql.Date.valueOf(checkin));
            params.add(java.sql.Date.valueOf(checkout));
            params.add(numberRoom);

            ps = preparedStatement(sql, params);

            rs = ps.executeQuery();

            if (rs.next()) {
                // Booking exists, return the BookingRoomsEntity
                BookingRoomsEntity bookingRoom = new BookingRoomsEntity();
                bookingRoom.setId(rs.getLong("ID"));
                bookingRoom.setUserId(rs.getInt("USER_ID"));
                bookingRoom.setEmail(rs.getString("EMAIL"));
                bookingRoom.setPhoneNumber(rs.getString("PHONE_NUMBER"));
                bookingRoom.setCheckin(rs.getDate("CHECK_IN"));
                bookingRoom.setCheckout(rs.getDate("CHECK_OUT"));
                bookingRoom.setTotalPrice(rs.getBigDecimal("TOTAL_PRICE"));
                bookingRoom.setNote(rs.getString("NOTE"));
                bookingRoom.setStatus(rs.getInt("STATUS"));
                bookingRoom.setTimeBooking(rs.getTimestamp("TIME_BOOKING").toLocalDateTime());
                bookingRoom.setPromotionId(rs.getInt("PROMOTION_ID"));
                bookingRoom.setLastModifyBy(rs.getInt("LAST_MODIFY_BY"));
                bookingRoom.setLastModifyDate(rs.getTimestamp("LAST_MODIFY_DATE").toLocalDateTime());
                bookingRoom.setLastName(rs.getString("LAST_NAME"));
                bookingRoom.setFirstName(rs.getString("FIRST_NAME"));

                return bookingRoom;
            }
        } catch (SQLException e) {
            log.error("Error checking booking existence: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return null;  // Return null if no booking exists with the given conditions
    }


    public BookingRoomsEntity updateStatusBookingRoom(long id, Integer newStatus) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = sqlLoader.getSql("UPDATE_STATUS_BOOKING_ROOM");

        try {
            List<Object> params = new ArrayList<>();
            params.add(newStatus);
            params.add(id);

            ps = preparedStatement(sql, params);

            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated > 0) {
                // If the update was successful, retrieve and return the updated booking room
                return getBookingRoomsById(id);
            }
        } catch (SQLException e) {
            log.error("Error updating status for booking room: " + e.getMessage(), e);
        } finally {
            closePS(ps);
        }

        return null;  // Return null if the update was not successful
    }



    //-----------------Booking_detail------------------//

    public void insertBookingRoomDetail(BookingRoomRequest request, BookingRoomDetailsEntity bookingRoomDetailsEntity) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = sqlLoader.getSql("INSERT_BOOKING_ROOM_DETAIL");

        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(request.getCheckin());
            ps = preparedStatement(sql);
            while (!calendar.getTime().after(request.getCheckout())) {
                Date currentDate = calendar.getTime();
                int idx = 0;
                String date = DateUtils.convertDateToString(currentDate);
                ps.setLong(++idx, bookingRoomDetailsEntity.getBookingRoomId());
                ps.setLong(++idx, bookingRoomDetailsEntity.getRoomId());
                ps.setLong(++idx, bookingRoomDetailsEntity.getNumberRoomBooking());
                ps.setLong(++idx, bookingRoomDetailsEntity.getNumberGuest());
                ps.setString(++idx, date);
                ps.setString(++idx, date);
                ps.setLong(++idx, request.getBookingRoomDetailsEntity().getNumberOfAdultsArising());
                ps.setLong(++idx, request.getBookingRoomDetailsEntity().getNumberOfChildArising());
                ps.setString(++idx, date);
                ps.setString(++idx, date);
                ps.setLong(++idx, bookingRoomDetailsEntity.getRoomId());
                ps.addBatch();
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            ps.executeBatch();


        } catch (SQLException e) {
            log.error("Error inserting booking room detail: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

    }

    public void createBookingRoomDetail(BookingRoomRequest request, BookingRoomDetailsEntity bookingRoomDetailsEntity, Map<String, BigDecimal> mapPriceCalender) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = sqlLoader.getSql("INSERT_BOOKING_ROOM_DETAIL");

        try {

            ps = preparedStatement(sql);
            for (Map.Entry<String, BigDecimal> entry : mapPriceCalender.entrySet()) {
                int idx = 0;
                String[] date = entry.getKey().split("/");
                ps.setLong(++idx, bookingRoomDetailsEntity.getBookingRoomId());
                ps.setLong(++idx, bookingRoomDetailsEntity.getRoomId());
                ps.setLong(++idx, bookingRoomDetailsEntity.getNumberRoomBooking());
                ps.setBigDecimal(++idx, entry.getValue());
                ps.setLong(++idx, bookingRoomDetailsEntity.getNumberGuest());
                ps.setString(++idx, date[0]);
                ps.setString(++idx, date[1]);
                ps.setObject(++idx, request.getBookingRoomDetailsEntity().getNumberOfAdultsArising());
                ps.setObject(++idx, request.getBookingRoomDetailsEntity().getNumberOfChildArising());
                ps.addBatch();
            }
            ps.executeBatch();

        } catch (SQLException e) {
            log.error("Error inserting booking room detail: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

    }


    public BookingRoomDetailsEntity getBookingRoomDetailsByBookingRoomId(long id) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = sqlLoader.getSql("GET_BOOKING_DETAIL_BY_ID");

        try {
            List<Object> params = new ArrayList<>();
            params.add(id);

            ps = preparedStatement(sql, params);

            rs = ps.executeQuery();

            if (rs.next()) {
                // Retrieve data from the result set and create a BookingRoomDetailsEntity
                BookingRoomDetailsEntity bookingRoomDetail = new BookingRoomDetailsEntity();
                bookingRoomDetail.setId(rs.getLong("ID"));
                bookingRoomDetail.setBookingRoomId((int) rs.getLong("BOOKING_ROOM_ID"));
                bookingRoomDetail.setRoomId((int) rs.getLong("ROOM_ID"));
                bookingRoomDetail.setNumberRoomBooking(rs.getInt("NUMBER_ROOM_BOOKING"));
                bookingRoomDetail.setPrice(rs.getBigDecimal("PRICE"));
                bookingRoomDetail.setNumberGuest(rs.getInt("NUMBER_GUEST"));

                return bookingRoomDetail;
            }
        } catch (SQLException e) {
            log.error("Error retrieving booking room detail by ID: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return null;  // Return null if no booking room detail with the given ID was found
    }

    public Integer getTotalBookingOutput(SearchBookingRequest request, Long staffId) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int totalRecords = 0;
        try {
            String sql = sqlLoader.getSql("COUNT_BOOKING_OUTPUT");
            sql = sql.replace(MasterDataConstants.EXTENDS_CONDITION,
                    HandlerUtils.createFullTextSearchQuery(listFieldSearchBooking, request.getSearchParams(), true));
            List<Object> params = new ArrayList<>();
            params.add(staffId);

            ps = preparedStatement(sql, params);

            rs = ps.executeQuery();
            while (rs.next()) {
                totalRecords = rs.getInt(1);
            }
            return totalRecords;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
    }

    public List<SearchBookingResponse> searchBooking(SearchBookingRequest request, Long staffId) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<SearchBookingResponse> listResponse = new ArrayList<>();
        String sql = sqlLoader.getSql("SELECT_BOOKING");
        sql = sql.replace(MasterDataConstants.EXTENDS_CONDITION,
                HandlerUtils.createFullTextSearchQuery(listFieldSearchBooking, request.getSearchParams(), true));
        StringBuilder pagingQuery = HandlerUtils.appendSortQuery(request.getSearchPaging());
        sql = sql.replace(MasterDataConstants.EXTENDS_PAGING, pagingQuery.toString());
        try {
            List<Object> params = new ArrayList<>();
            params.add(staffId);
            ps = preparedStatement(sql, params);

            rs = ps.executeQuery();

            while (rs.next()) {
                SearchBookingResponse response = new SearchBookingResponse();
                response.setId(rs.getLong("id"));
                response.setCustomerName(rs.getString("customer_name"));
                response.setCustomerEmail(rs.getString("email"));
                response.setCustomerPhoneNumber(rs.getString("phone_number"));
                response.setCheckin(rs.getDate("check_in"));
                response.setCheckout(rs.getDate("check_out"));
                response.setActualPrice(rs.getBigDecimal("total_price"));
                int status = rs.getInt("status");
                if(status == BookingRoomStatusConstants.PENDING) {
                    response.setStatus("Chờ thanh toán");
                } else if(status == BookingRoomStatusConstants.IN_PROGRESS) {
                    response.setStatus("Đã thanh toán");
                } else if(status == BookingRoomStatusConstants.CHECK_IN) {
                    response.setStatus("Check in");
                } else if(status == BookingRoomStatusConstants.DONE) {
                    response.setStatus("Đã hoàn thành");
                } else if (status == BookingRoomStatusConstants.CANCEL) {
                    response.setStatus("Hủy");
                } else if (status == BookingRoomStatusConstants.CANCEL_BY_HOTEL) {
                    response.setStatus("Hủy bởi khách sạn");
                } else if (status == BookingRoomStatusConstants.REFUND) {
                    response.setStatus("Hoàn tiền");
                }
                listResponse.add(response);
            }
            return listResponse;
        } catch (SQLException e) {
            log.error("Error: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return null;
    }

    public GetBookingDetailByIdResponse getBookingDetailById(Long staffId, Long id) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = sqlLoader.getSql("SELECT_BOOKING_DETAIL_BY_ID");

        try {
            List<Object> params = new ArrayList<>();
            params.add(staffId);
            params.add(id);

            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();

            if (rs.next()) {
                GetBookingDetailByIdResponse response = new GetBookingDetailByIdResponse();
                    response.setId(rs.getLong("id"));
                    response.setCustomerName(rs.getString("customer_name"));
                    response.setCustomerEmail(rs.getString("email"));
                    response.setCustomerPhoneNumber(rs.getString("phone_number"));
                    response.setCheckin(rs.getDate("check_in"));
                    response.setCheckout(rs.getDate("check_out"));
                    response.setTotalPrice(rs.getBigDecimal("price"));
                    response.setActualPrice(rs.getBigDecimal("total_price"));
                    response.setNote(rs.getString("note"));
                    response.setTimeBooking(rs.getTimestamp("time_booking"));
                    response.setPromotionCode(rs.getString("promotion_code"));
                    response.setRoomId(rs.getLong("room_id"));
                    response.setRoomName(rs.getString("room_name"));
                    response.setRoomCode(rs.getString("room_code"));
                    response.setNumberRoomBooking(rs.getInt("number_room_booking"));
                    response.setListRoomImage(getListRoomImage(response.getRoomId()));
                    response.setListBookingDetail(getListBookingDetail(response.getId()));
                return response;
            } else {
                log.warn("No booking found with ID: " + id);
            }
        } catch (SQLException e) {
            log.error("Error retrieving booking by ID: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return null;
    }

    public List<GetBookingDetailByIdResponse.BookingDetail> getListBookingDetail(Long id) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<GetBookingDetailByIdResponse.BookingDetail> list = new ArrayList<>();

        String sql = sqlLoader.getSql("SELECT_BOOKING_DETAIL_BY_BOOKING_ID");

        try {
            List<Object> params = new ArrayList<>();
            params.add(id);

            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();

            while (rs.next()) {
                GetBookingDetailByIdResponse.BookingDetail response = new GetBookingDetailByIdResponse.BookingDetail();
                response.setFromDate(rs.getDate("from_date"));
                response.setToDate(rs.getDate("to_date"));
                response.setPrice(rs.getBigDecimal("price"));
                response.setNumberGuest(rs.getInt("number_guest"));
                response.setNumberRoom(rs.getInt("number_room_booking"));
                response.setNumberOfAdults(rs.getInt("number_of_adults_arising"));
                response.setNumberOfChild(rs.getInt("number_of_child_arising"));
                list.add(response);
            }
            return list;
        } catch (SQLException e) {
            log.error("Error retrieving booking by ID: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
        return null;
    }

    public List<String> getListRoomImage(Long roomId) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<String> list = new ArrayList<>();

        String sql = sqlLoader.getSql("SELECT_ROOM_IMAGE_BY_ROOM_ID");

        try {
            List<Object> params = new ArrayList<>();
            params.add(roomId);

            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();

            while (rs.next()) {
                String image = rs.getString("image_url");
                list.add(image);
            }
            return list;
        } catch (SQLException e) {
            log.error("Error retrieving booking by ID: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
        return null;
    }

    public int totalBookingSystem() throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int totalRecords = 0;
        try {
            String sql = sqlLoader.getSql("COUNT_TOTAL_BOOKING_SYSTEM");
            ps = preparedStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                totalRecords = rs.getInt(1);
            }
            return totalRecords;
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
    }

    public static Timestamp addDays(Timestamp date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return new Timestamp(cal.getTime().getTime());

    }

    public List<TotalBookingWeeklyResponse.BookingWeekly> totalBookingByWeeklySystem(Timestamp monday, Timestamp sunday) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<TotalBookingWeeklyResponse.BookingWeekly> listData = new ArrayList<>();

        try {
            String sql = sqlLoader.getSql("COUNT_TOTAL_BOOKING_BY_WEEKLY_SYSTEM");

            List<Object> params = new ArrayList<>();
            params.add(monday);
            params.add(addDays(sunday, 1));

            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();
            while (rs.next()) {
                TotalBookingWeeklyResponse.BookingWeekly temp = new TotalBookingWeeklyResponse.BookingWeekly();

                Timestamp date = rs.getTimestamp("day");
                LocalDateTime localDateTime = date.toLocalDateTime();

                temp.setDay(localDateTime.getDayOfMonth());
                temp.setNumberBooking(rs.getInt("total"));
                listData.add(temp);
            }
            return listData;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
    }

    public int totalBookingBusinessAdmin(Long businessAdminId) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int totalRecords = 0;
        try {
            String sql = sqlLoader.getSql("COUNT_TOTAL_BOOKING_BUSINESS_ADMIN");

            List<Object> params = new ArrayList<>();
            params.add(businessAdminId);

            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();
            while (rs.next()) {
                totalRecords = rs.getInt(1);
            }
            return totalRecords;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
    }

    public List<TotalBookingWeeklyResponse.BookingWeekly> totalBookingByWeeklyBusinessAdmin(Timestamp monday, Timestamp sunday, Long businessAdminId) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<TotalBookingWeeklyResponse.BookingWeekly> listData = new ArrayList<>();

        try {
            String sql = sqlLoader.getSql("COUNT_TOTAL_BOOKING_BY_WEEKLY_BUSINESS_ADMIN");

            List<Object> params = new ArrayList<>();
            params.add(monday);
            params.add(addDays(sunday, 1));
            params.add(businessAdminId);
            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();
            while (rs.next()) {
                TotalBookingWeeklyResponse.BookingWeekly temp = new TotalBookingWeeklyResponse.BookingWeekly();

                Timestamp date = rs.getTimestamp("day");
                LocalDateTime localDateTime = date.toLocalDateTime();

                temp.setDay(localDateTime.getDayOfMonth());
                temp.setNumberBooking(rs.getInt("total"));
                listData.add(temp);
            }
            return listData;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
    }

    public int totalBookingBusinessOwner(Long businessOwnerId) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int totalRecords = 0;
        try {
            String sql = sqlLoader.getSql("COUNT_TOTAL_BOOKING_BUSINESS_OWNER");

            List<Object> params = new ArrayList<>();
            params.add(businessOwnerId);

            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();
            while (rs.next()) {
                totalRecords = rs.getInt(1);
            }
            return totalRecords;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
    }

    public List<TotalBookingWeeklyResponse.BookingWeekly> totalBookingByWeeklyBusinessOwner(Timestamp monday, Timestamp sunday, Long businessOwnerId) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<TotalBookingWeeklyResponse.BookingWeekly> listData = new ArrayList<>();

        try {
            String sql = sqlLoader.getSql("COUNT_TOTAL_BOOKING_BY_WEEKLY_BUSINESS_OWNER");

            List<Object> params = new ArrayList<>();
            params.add(monday);
            params.add(addDays(sunday, 1));
            params.add(businessOwnerId);
            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();
            while (rs.next()) {
                TotalBookingWeeklyResponse.BookingWeekly temp = new TotalBookingWeeklyResponse.BookingWeekly();

                Timestamp date = rs.getTimestamp("day");
                LocalDateTime localDateTime = date.toLocalDateTime();

                temp.setDay(localDateTime.getDayOfMonth());
                temp.setNumberBooking(rs.getInt("total"));
                listData.add(temp);
            }
            return listData;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
    }

    public HistoryBookingResponse historyBooking(Long userId, HistoryBookingRequest request) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<HistoryBookingResponse.HistoryBooking> listBookingDone = new ArrayList<>();
        List<HistoryBookingResponse.HistoryBooking> listBookingPending = new ArrayList<>();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        String temp;
        if(request.getCheckin() == null && request.getCheckout() == null) {
            temp = "";
        } else if(request.getCheckin() == null && request.getCheckout() != null) {
            temp = "AND br.check_out <= '" + format.format(request.getCheckout()) + "' ";
        } else if(request.getCheckin() != null && request.getCheckout() == null) {
            temp = "AND br.check_in >= '" + format.format(request.getCheckin()) +"' ";
        } else {
            temp = "AND br.check_in >= '"+ format.format(request.getCheckin()) +"' AND br.check_out <= '"+ format.format(request.getCheckout()) +"' ";
        }

        try {
            String sql = sqlLoader.getSql("SELECT_HISTORY_BOOKING");
            sql = sql.replace("#EXTEND_CONDITION#", temp);
            List<Object> params = new ArrayList<>();
            params.add(userId);

            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();
            while (rs.next()) {
                HistoryBookingResponse.HistoryBooking b = new HistoryBookingResponse.HistoryBooking();
                b.setBookingId(rs.getLong("id"));
                b.setHotelId(rs.getLong("hotel_id"));
                b.setHotelName(rs.getString("hotel_name"));
                b.setRoomName(rs.getString("room_name"));
                b.setAddress(rs.getString("address"));
                b.setCheckIn(rs.getDate("check_in"));
                b.setCheckOut(rs.getDate("check_out"));
                b.setTotalPrice(rs.getBigDecimal("total_price"));
                b.setNumberRoomBooking(rs.getInt("number_room_booking"));
                b.setStatus(rs.getInt("status"));
                b.setHotelImage(rs.getString("image_url"));
                if (Objects.equals(b.getStatus(), BookingRoomStatusConstants.DONE)
                    || Objects.equals(b.getStatus(), BookingRoomStatusConstants.IN_PROGRESS)
                        || Objects.equals(b.getStatus(), BookingRoomStatusConstants.CHECK_IN)
                ) {
                    listBookingDone.add(b);
                } else if(Objects.equals(b.getStatus(), BookingRoomStatusConstants.PENDING)
                    || Objects.equals(b.getStatus(), BookingRoomStatusConstants.CANCEL)
                )  {
                    listBookingPending.add(b);
                }
            }
            HistoryBookingResponse result = new HistoryBookingResponse();
            result.setListBookingDone(listBookingDone);
            result.setListBookingPending(listBookingPending);
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
    }

    public List<BookingRoomsEntity> getAllBookingRooms() {
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = sqlLoader.getSql("GET_ALL_BOOKING_ROOM");

        try {
            ps = preparedStatement(sql, new ArrayList<>());

            rs = ps.executeQuery();

            List<BookingRoomsEntity> bookingRoomsList = new ArrayList<>();

            while (rs.next()) {
                BookingRoomsEntity bookingRoom = mapResultSetToBookingRooms(rs);
                bookingRoomsList.add(bookingRoom);
            }

            return bookingRoomsList;
        } catch (SQLException e) {
            log.error("Error retrieving all booking rooms: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return new ArrayList<>();  // Return an empty list if there is an error or no booking rooms found
    }

    // Helper method to map ResultSet to BookingRoomsEntity
    private BookingRoomsEntity mapResultSetToBookingRooms(ResultSet rs) throws SQLException {
        BookingRoomsEntity bookingRoom = new BookingRoomsEntity();
        bookingRoom.setId(rs.getLong("ID"));
        bookingRoom.setUserId(rs.getInt("USER_ID"));
        bookingRoom.setEmail(rs.getString("EMAIL"));
        bookingRoom.setPhoneNumber(rs.getString("PHONE_NUMBER"));
        bookingRoom.setCheckin(rs.getDate("CHECK_IN"));
        bookingRoom.setCheckout(rs.getDate("CHECK_OUT"));
        bookingRoom.setTotalPrice(rs.getBigDecimal("TOTAL_PRICE"));
        bookingRoom.setNote(rs.getString("NOTE"));
        bookingRoom.setStatus(rs.getInt("STATUS"));
        bookingRoom.setTimeBooking(rs.getTimestamp("TIME_BOOKING").toLocalDateTime());
        bookingRoom.setPromotionId(rs.getInt("PROMOTION_ID"));
        bookingRoom.setLastModifyBy(rs.getInt("LAST_MODIFY_BY"));
        bookingRoom.setLastModifyDate(rs.getTimestamp("LAST_MODIFY_DATE").toLocalDateTime());
        bookingRoom.setLastName(rs.getString("LAST_NAME"));
        bookingRoom.setFirstName(rs.getString("FIRST_NAME"));

        return bookingRoom;
    }

    public GetBookingDetailByIdResponse getBookingDetailByIdByQr(Long id) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = sqlLoader.getSql("SELECT_BOOKING_DETAIL_BY_ID");

        try {
            List<Object> params = new ArrayList<>();
            params.add(id);

            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();

            if (rs.next()) {
                GetBookingDetailByIdResponse response = new GetBookingDetailByIdResponse();
                response.setId(rs.getLong("id"));
                response.setCustomerName(rs.getString("customer_name"));
                response.setCustomerEmail(rs.getString("email"));
                response.setCustomerPhoneNumber(rs.getString("phone_number"));
                response.setCheckin(rs.getDate("check_in"));
                response.setCheckout(rs.getDate("check_out"));
                response.setTotalPrice(rs.getBigDecimal("price"));
                response.setActualPrice(rs.getBigDecimal("total_price"));
                response.setNote(rs.getString("note"));
                response.setTimeBooking(rs.getTimestamp("time_booking"));
                response.setPromotionCode(rs.getString("promotion_code"));
                response.setRoomId(rs.getLong("room_id"));
                response.setRoomName(rs.getString("room_name"));
                response.setRoomCode(rs.getString("room_code"));
                response.setNumberRoomBooking(rs.getInt("number_room_booking"));
                response.setListRoomImage(getListRoomImage(response.getRoomId()));
                response.setListBookingDetail(getListBookingDetail(response.getId()));
                return response;
            } else {
                log.warn("No booking found with ID: " + id);
            }
        } catch (SQLException e) {
            log.error("Error retrieving booking by ID: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return null;
    }

    public BookingRoomsEntity getBookingQrById(Long staffId, Long id) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = sqlLoader.getSql("SELECT_BOOKING_DETAIL_QR_BY_ID");

        try {
            List<Object> params = new ArrayList<>();
            params.add(staffId);
            params.add(id);

            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();

            if (rs.next()) {
                BookingRoomsEntity entity = new BookingRoomsEntity();
                entity.setId(rs.getLong("id"));
                entity.setStatus(rs.getInt("status"));
                return entity;
            } else {
                log.warn("No booking found with ID: " + id);
            }
        } catch (SQLException e) {
            log.error("Error retrieving booking by ID: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return null;
    };
}
