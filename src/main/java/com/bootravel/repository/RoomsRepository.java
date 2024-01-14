package com.bootravel.repository;

import com.bootravel.common.CommonRepository;
import com.bootravel.common.constant.MasterDataConstants;
import com.bootravel.entity.Price;
import com.bootravel.entity.RoomAvailableByDatesEntity;
import com.bootravel.entity.RoomTypesEntity;
import com.bootravel.entity.RoomsEntity;
import com.bootravel.payload.requests.*;
import com.bootravel.payload.requests.commonRequests.RoomFilterRequest;
import com.bootravel.payload.responses.*;
import com.bootravel.utils.DateUtils;
import com.bootravel.utils.HandlerUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.xml.sax.SAXException;

import javax.validation.constraints.NotNull;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

@Repository
public class RoomsRepository extends CommonRepository {
    private List<String> listFieldSearch = Arrays.asList("ROOM_NAME", "ROOM_CODE");

    public RoomsRepository() throws ParserConfigurationException, IOException, SAXException {
        super();
    }

    @Override
    protected String getFileKey() {
        return "/sql/sqlRoomsRepository.xml";
    }

    public Long getSeqRoom() throws Exception {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("SEQ_ROOM_ID");
        try {
            ps = preparedStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong("ROOM_ID");
            }
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
        return 0L;
    }

    public void createRooms(CreateRoomRequest request, Long roomId, Long userId) throws Exception {
        PreparedStatement ps = null;

        String sql = sqlLoader.getSql("INSERT_ROOM");
        List<Object> params = new ArrayList<>();
        params.add(roomId);
        params.add(request.getRoomName());
        params.add(request.getRoomCount());
        params.add(request.getRoomSize());
        params.add(request.getRoomTypeId());
        params.add(request.getMinPrice());
        params.add(request.getDefaultPrice());
        params.add(request.getWeekPrice());
        params.add(request.getMonthPrice());
        params.add(request.getAdditionalAdultFee());
        params.add(request.getAdditionalChildFee());
        params.add(request.getMaxPeopleStay());
        params.add(request.getPriceIncludesBreakfast());
        params.add(request.getConfirmationWithinMinute());
        params.add(request.getSurchargeForAdultChild());
        params.add(request.getConfirmNow());
        params.add(request.getRoomCode());
        params.add(request.getStandardNumberOfPeople());
        params.add(userId);
        try {
            ps = preparedStatement(sql, params);
            ps.executeUpdate();
        } finally {
            closePS(ps);
        }
    }

    public void insertBedInRoom(Map<Long, Long> mapBedInRoom, Long roomId) throws Exception {
        PreparedStatement ps = null;

        String sql = sqlLoader.getSql("INSERT_BED_IN_ROOM");
        try {
            ps = preparedStatement(sql);
            for (Map.Entry<Long, Long> bedInRoom : mapBedInRoom.entrySet()) {

                int idx = 0;
                ps.setLong(++idx, roomId);
                ps.setLong(++idx, bedInRoom.getKey());
                ps.setLong(++idx, bedInRoom.getValue());
                ps.addBatch();
            }
            ps.executeBatch();
        } finally {
            closePS(ps);
        }
    }

    public void insertRoomService(Long roomId, List<Long> listService) throws SQLException {
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("INSERT_ROOM_SERVICE");
        try {
            ps = preparedStatement(sql);
            for (Long serviceId : listService) {
                int idx = 0;
                ps.setLong(++idx, roomId);
                ps.setLong(++idx, serviceId);
                ps.addBatch();
            }
            ps.executeBatch();
        } finally {
            closePS(ps);
        }
    }

    public void setPriceByDate(SetPriceByDateRequest request) throws Exception {
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("SET_ROOM_PRICE_BY_DATE");
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(request.getDateFrom());
            ps = preparedStatement(sql);
            while (!calendar.getTime().after(request.getDateTo())) {
                Date currentDate = calendar.getTime();
                int idx = 0;
                ps.setLong(++idx, request.getRoomId());
                ps.setString(++idx, DateUtils.convertDateToString(currentDate));
                ps.setLong(++idx, request.getPrice());
                ps.addBatch();
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            ps.executeBatch();
        } finally {
            closePS(ps);
        }
    }

    public void setRoomAvailableByDate(SetRoomAvailableByDateRequest request) throws Exception {
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("SET_NUM_ROOM_AVAILABLE_BY_DATE");
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(DateUtils.convertStringToDate(request.getDateFrom(),"yyyy-MM-dd"));
            ps = preparedStatement(sql);
            while (!calendar.getTime().after(DateUtils.convertStringToDate(request.getDateTo(),"yyyy-MM-dd"))) {
                Date currentDate = calendar.getTime();
                int idx = 0;
                ps.setLong(++idx, request.getRoomId());
                ps.setString(++idx, DateUtils.convertDateToString(currentDate));
                ps.setLong(++idx, request.getNumRoomAvailable());
                ps.addBatch();
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            ps.executeBatch();
        } finally {
            closePS(ps);
        }
    }

    public List<RoomsEntity> filterRoom(CheckRoomByDateRequest filterParams) throws Exception {
        ResultSet rs = null;
        PreparedStatement ps = null;
        List<RoomsEntity> roomsList = new ArrayList<>();
        String sql = sqlLoader.getSql("SEARCH_ROOM");

        try {
            ps = preparedStatement(sql);
            int paramIndex = 1;
            // Set parameters using positional placeholders

            if (filterParams.getUserMaxPeopleStay() != null) {
                ps.setInt(paramIndex++, filterParams.getUserMaxPeopleStay());
            } else {
                ps.setNull(paramIndex++, Types.INTEGER);
            }

            if (filterParams.getUserStartDateApply() != null) {
                ps.setDate(paramIndex++, new java.sql.Date(filterParams.getUserStartDateApply().getTime()));
            } else {
                ps.setNull(paramIndex++, Types.DATE);
            }

            if (filterParams.getUserEndDateApply() != null) {
                ps.setDate(paramIndex++, new java.sql.Date(filterParams.getUserEndDateApply().getTime()));
            } else {
                ps.setNull(paramIndex++, Types.DATE);
            }

            if (filterParams.getNumberRoom() != null) {
                ps.setInt(paramIndex++, filterParams.getNumberRoom());
            } else {
                ps.setNull(paramIndex++, Types.INTEGER);
            }
            rs = ps.executeQuery();

            while (rs.next()) {
                RoomsEntity room = new RoomsEntity();
                room.setId(rs.getLong("ID"));
                room.setName(rs.getString("ROOM_NAME"));
                room.setRoomCount(rs.getInt("ROOM_COUNT"));
                room.setRoomSize(rs.getInt("ROOM_SIZE"));
                room.setRoomTypeId(rs.getInt("ROOM_TYPE_ID"));
                room.setMinPrice(rs.getBigDecimal("MIN_PRICE"));
                room.setDefaultPrice(rs.getBigDecimal("DEFAULT_PRICE"));
                room.setWeekPrice(rs.getBigDecimal("WEEK_PRICE"));
                room.setMonthPrice(rs.getBigDecimal("MONTH_PRICE"));
                room.setAdditionalAdultFee(rs.getBigDecimal("ADDITIONAL_ADULT_FEE"));
                room.setAdditionalChildFee(rs.getBigDecimal("ADDITIONAL_CHILD_FEE"));
                room.setMaxPeopleStay(rs.getInt("MAX_PEOPLE_STAY"));
                room.setStatus(rs.getBoolean("STATUS"));
                room.setHotelId(rs.getInt("HOTEL_ID"));
                room.setPriceIncludesBreakfast(rs.getBoolean("PRICE_INCLUDES_BREAKFAST"));
                room.setComfirmationWithinMinute(rs.getBoolean("confirmation_within_30_minutes"));
                room.setSurchargeForAdultChild(rs.getBoolean("surcharge_for_adults_children"));
                room.setComfirmNow(rs.getBoolean("confirm_now"));

                // Add the RoomsEntity object to the list
                roomsList.add(room);
            }
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return roomsList;
    }


    public boolean checkMaxRoomAvailable(SetRoomAvailableByDateRequest request) throws Exception {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("CHECK_MAX_ROOM_AVAILABLE");
        try {
            List<Object> params = new ArrayList<>();
            params.add(request.getNumRoomAvailable());
            params.add(request.getDateFrom());
            params.add(request.getDateTo());
            params.add(request.getRoomId());
            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getBoolean("CHECK")) {
                    return true;
                }
            }
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
        return false;
    }


    public List<RoomAvailableByDatesEntity> searchRoomsAvailableCalender(CalenderRoomRequest request) throws Exception {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("SEARCH_ROOM_AVAILABLE_CALENDER");
        List<Object> params = new ArrayList<>();
        params.add(request.getDateFrom());
        params.add(request.getDateTo());
        params.add(request.getRoomId());
        List<RoomAvailableByDatesEntity> list = new ArrayList<>();
        try {
            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();
            while (rs.next()) {
                RoomAvailableByDatesEntity entity = new RoomAvailableByDatesEntity();
                entity.setDateApply(rs.getDate("DATE_TIME"));
                entity.setNumberRoomAvailable(rs.getLong("NUMBER_ROOM_AVAILABLE"));
                entity.setTotalRoomBooking(rs.getLong("TOTAL_NUMBER_ROOM_BOOKING"));
                list.add(entity);
            }
        } catch (SQLException e) {
            log.info(e.getMessage(), e);
            return null;
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return list;

    }

    public BigDecimal getPriceDefault(Long id) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = sqlLoader.getSql("GET_PRICE_DEFAULT");
        try {
            ps = preparedStatement(sql);
            int idx = 0;
            ps.setLong(++idx, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal("DEFAULT_PRICE");
            }
        } catch (SQLException e) {
            log.info(e.getMessage(), e);
            return null;
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
        return null;
    }

    public Map<Date, BigDecimal> searchRoomPriceCalender(CalenderRoomRequest request) throws Exception {
        Map<Date, BigDecimal> mapData = new HashMap<>();
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("SEARCH_ROOM_PRICE_CALENDER");

        List<Object> params = new ArrayList<>();
        params.add(request.getRoomId());
        params.add(DateUtils.convertDateToString(request.getDateFrom()));
        params.add(DateUtils.convertDateToString(request.getDateTo()));

        try {
            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();
            while (rs.next()) {
                mapData.put(rs.getDate("DATE_APPLY"), rs.getBigDecimal("PRICE"));
            }
        } catch (SQLException e) {
            log.info(e.getMessage(), e);
            return null;
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return mapData;

    }

    public Map<Long, Price> searchListRoomPriceCalender(Date from, Date to, List<RoomsEntity> listRooms) throws Exception {
        Map<Long, Price> mapAllRoom = new LinkedHashMap<>();
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("SEARCH_LIST_ROOM_PRICE_CALENDER");
        List<Object> params = new ArrayList<>();
        params.add(from);
        params.add(to);
        params.add(from);
        params.add(to);
        params.add(to);
        sql = sql.replace("#CONDITION", listRooms.stream().map(item -> String.valueOf(item.getId())).collect(Collectors.joining(",")));
        List<Long> listIdRoom = new ArrayList<>();
        try {
            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();
            while (rs.next()) {

                if ((!CollectionUtils.isEmpty(listIdRoom) && !listIdRoom.contains(rs.getLong("ID")))
                        || CollectionUtils.isEmpty(listIdRoom)) {
                    long id = rs.getLong("ID");
                    listIdRoom.add(id);

                    ResultSet finalRs = rs;
                    Map<String, BigDecimal> mapPriceByDate = new LinkedHashMap<String, BigDecimal>() {{
                        put(finalRs.getString("DATE_TIME"),
                                Objects.nonNull(finalRs.getBigDecimal("PRICE")) ?
                                        finalRs.getBigDecimal("PRICE") :
                                        (listRooms.stream()
                                                .filter(obj -> Objects.equals(id, obj.getId()))
                                                .findFirst()
                                                .orElse(null)) != null ?
                                                listRooms.stream()
                                                        .filter(obj -> Objects.equals(id, obj.getId()))
                                                        .findFirst()
                                                        .get()
                                                        .getDefaultPrice() :
                                                BigDecimal.ZERO);
                    }};
                    mapAllRoom.put(id, new Price(rs.getBigDecimal("MONTH_PRICE"),
                            rs.getBigDecimal("WEEK_PRICE"), mapPriceByDate));
                } else {
                    mapAllRoom.get(rs.getLong("ID")).getMapPriceByDate().put(rs.getString("DATE_TIME"),
                            rs.getBigDecimal("PRICE"));
                }
            }
        } catch (SQLException e) {
            log.info(e.getMessage(), e);
            return null;
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }



        return mapAllRoom;

    }

    public List<RoomResponse> searchRoomManagements(Long userId, SearchRoomRequest request) throws Exception {
        List<RoomResponse> responseList = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement ps = null;

        String sortBy = setSortBySqlColumnName(request.getBaseSearchPagingDTO().getSortBy());
        request.getBaseSearchPagingDTO().setSortBy(sortBy);

        String sql = sqlLoader.getSql("SEARCH_ROOM_USER_MANAGEMENT");

        sql = sql.replace(MasterDataConstants.EXTENDS_CONDITION,
                HandlerUtils.createFullTextSearchQuery(listFieldSearch, request.getSearchParams(), true));
        StringBuilder pagingQuery = HandlerUtils.appendSortQuery(request.getBaseSearchPagingDTO());
        sql = sql.replace(MasterDataConstants.EXTENDS_PAGING, pagingQuery.toString());

        try {
            ps = preparedStatement(sql);
            int idx = 0;
            ps.setLong(++idx, userId);
            rs = ps.executeQuery();
            while (rs.next()) {
                RoomResponse response = new RoomResponse();
                response.setId(rs.getLong("ID"));
                response.setName(rs.getString("ROOM_NAME"));
                response.setRoomCode(rs.getString("ROOM_CODE"));
                response.setRoomType(rs.getString("ROOM_TYPE_NAME"));
                response.setRoomCount(rs.getInt("ROOM_COUNT"));
                response.setRoomSize(rs.getInt("ROOM_SIZE"));
                response.setMaxPeopleStay(rs.getInt("MAX_PEOPLE_STAY"));
                response.setDefaultPrice(rs.getBigDecimal("DEFAULT_PRICE"));
                response.setStatus(rs.getBoolean("STATUS"));
                responseList.add(response);
            }
        } catch (SQLException e) {
            log.info(e.getMessage(), e);
            return null;
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return responseList;
    }

    public StringBuilder appendSqlSearchRoomCondition(SearchRoomRequest searchConditon) {
        StringBuilder sqlBuilder = new StringBuilder();
        // with 部署選択 condition
        if (StringUtils.isNotEmpty(searchConditon.getSearchParams())) {
            String[] condition = searchConditon.getSearchParams().split(MasterDataConstants.COMMA);
            sqlBuilder.append(MasterDataConstants.SQL_AND).append(HandlerUtils.appendInCondition("ROOM_NAME", condition))
                    .append(MasterDataConstants.SQL_OR)
                    .append(HandlerUtils.appendInCondition("ROOM_CODE", condition))
            ;
        }
        return sqlBuilder;
    }

    public int getTotalRoom(Long userId, SearchRoomRequest request) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int totalRecords = 0;
        try {
            //ＳＱＬ文の取得
            String sql = sqlLoader.getSql("COUNT_ROOM_OUTPUT");
            sql = sql.replace(MasterDataConstants.EXTENDS_CONDITION,
                    HandlerUtils.createFullTextSearchQuery(listFieldSearch, request.getSearchParams(), true));
            // excute query
            ps = preparedStatement(sql);

            int idx = 0;
            ps.setLong(++idx, userId);

            //ＳＱＬ実行
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

    public String setSortBySqlColumnName(String orderBy) {
        switch (orderBy) {
            case "roomName":
                return "HOTEL_NAME";
            case "id":
                return "ID";
            case "roomCount":
                return "ROOM_COUNT";
            default:
                return "";
        }
    }

    public BigDecimal getPriceByDateRoom(long roomId, Date dateApply) throws Exception {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("GET_PRICE_BY_DATE_AND_ROOM_ID");

        try {
            ps = preparedStatement(sql);
            int idx = 0;
            ps.setLong(++idx, roomId);
            ps.setDate(++idx, new java.sql.Date(dateApply.getTime()));
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal("PRICE");
            }
        } catch (SQLException e) {
            log.info(e.getMessage(), e);
           return null;
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
     return null;
    }

    public BigDecimal getPriceByDateRoomVerTwo(long roomId, Date dateApply) throws Exception {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("GET_PRICE_BY_DATE_AND_ROOM_ID");

        try {
            ps = preparedStatement(sql);
            int idx = 0;
            ps.setLong(++idx, roomId);
            ps.setDate(++idx, new java.sql.Date(dateApply.getTime()));
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal("PRICE");
            }
        } catch (SQLException e) {
            log.info(e.getMessage(), e);
            return null;
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
        return null;
    }

    public RoomsEntity getRoomById(long id) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("GET_ROOM_BY_ID");

        try {
            // Create a list to hold the query parameters
            List<Object> params = new ArrayList<>();
            params.add(id);

            // Create the prepared statement with parameters
            ps = preparedStatement(sql, params);

            // Execute the query and retrieve the result set
            rs = ps.executeQuery();

            // Check if there's a result
            if (rs.next()) {
                // Create a RoomsEntity object and populate it with data from the result set
                RoomsEntity room = new RoomsEntity();
                room.setId(rs.getLong("ID"));
                room.setName(rs.getString("ROOM_NAME"));
                room.setRoomCount(rs.getInt("ROOM_COUNT"));
                room.setRoomSize(rs.getInt("ROOM_SIZE"));
                room.setRoomTypeId(rs.getInt("ROOM_TYPE_ID"));
                room.setMinPrice(rs.getBigDecimal("MIN_PRICE"));
                room.setDefaultPrice(rs.getBigDecimal("DEFAULT_PRICE"));
                room.setWeekPrice(rs.getBigDecimal("WEEK_PRICE"));
                room.setMonthPrice(rs.getBigDecimal("MONTH_PRICE"));
                room.setAdditionalAdultFee(rs.getBigDecimal("ADDITIONAL_ADULT_FEE"));
                room.setAdditionalChildFee(rs.getBigDecimal("ADDITIONAL_CHILD_FEE"));
                room.setMaxPeopleStay(rs.getInt("MAX_PEOPLE_STAY"));
                room.setStatus(rs.getBoolean("STATUS"));
                room.setHotelId(rs.getInt("HOTEL_ID"));
                room.setPriceIncludesBreakfast(rs.getBoolean("PRICE_INCLUDES_BREAKFAST"));
                room.setComfirmationWithinMinute(rs.getBoolean("confirmation_within_30_minutes"));
                room.setSurchargeForAdultChild(rs.getBoolean("surcharge_for_adults_children"));
                room.setComfirmNow(rs.getBoolean("confirm_now"));
                room.setRoomCode(rs.getString("ROOM_CODE"));
                room.setStandardNumberOfPeople(rs.getLong("STANDARD_NUMBER_OF_PEOPLE"));
                if (StringUtils.isNotEmpty(rs.getString("LIST_SERVICE"))){
                    room.setListService(Arrays.asList(rs.getString("LIST_SERVICE").split(",")));
                }
                return room;
            }

        } catch (SQLException e) {
            log.error("Error retrieving room by ID: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return null; // Return null if no room with the provided ID was found
    }

    public RoomTypesEntity getRoomTypeById(long id) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("GET_ROOM_TYPE_BY_ID");

        try {
            // Create a list to hold the query parameters
            List<Object> params = new ArrayList<>();
            params.add(id);

            // Create the prepared statement with parameters
            ps = preparedStatement(sql, params);

            // Execute the query and retrieve the result set
            rs = ps.executeQuery();

            // Check if there's a result
            if (rs.next()) {
                // Create a RoomsEntity object and populate it with data from the result set
                RoomTypesEntity room = new RoomTypesEntity();
                room.setId(rs.getLong("ID"));
                room.setName(rs.getString("room_type_name"));
                return room;
            }
        } catch (SQLException e) {
            log.error("Error retrieving room by ID: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return null; // Return null if no room with the provided ID was found
    }

    public List<RoomsEntity> getRoomByHotelId(Integer id) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("GET_ROOM_BY_HOTEL_ID");
        List<RoomsEntity> roomsList = new ArrayList<>();

        try {
            // Create a list to hold the query parameters
            List<Object> params = new ArrayList<>();
            params.add(id);

            // Create the prepared statement with parameters
            ps = preparedStatement(sql, params);

            // Execute the query and retrieve the result set
            rs = ps.executeQuery();

            // Iterate through the result set
            while (rs.next()) {
                // Create a RoomsEntity object for each row and populate it with data from the result set
                RoomsEntity room = new RoomsEntity();
                room.setId(rs.getLong("ID"));
                room.setName(rs.getString("ROOM_NAME"));
                room.setRoomCount(rs.getInt("ROOM_COUNT"));
                room.setRoomSize(rs.getInt("ROOM_SIZE"));
                room.setRoomTypeId(rs.getInt("ROOM_TYPE_ID"));
                room.setMinPrice(rs.getBigDecimal("MIN_PRICE"));
                room.setDefaultPrice(rs.getBigDecimal("DEFAULT_PRICE"));
                room.setWeekPrice(rs.getBigDecimal("WEEK_PRICE"));
                room.setMonthPrice(rs.getBigDecimal("MONTH_PRICE"));
                room.setAdditionalAdultFee(rs.getBigDecimal("ADDITIONAL_ADULT_FEE"));
                room.setAdditionalChildFee(rs.getBigDecimal("ADDITIONAL_CHILD_FEE"));
                room.setMaxPeopleStay(rs.getInt("MAX_PEOPLE_STAY"));
                room.setStatus(rs.getBoolean("STATUS"));
                room.setHotelId(rs.getInt("HOTEL_ID"));
                room.setPriceIncludesBreakfast(rs.getBoolean("PRICE_INCLUDES_BREAKFAST"));
                room.setComfirmationWithinMinute(rs.getBoolean("confirmation_within_30_minutes"));
                room.setSurchargeForAdultChild(rs.getBoolean("surcharge_for_adults_children"));
                room.setComfirmNow(rs.getBoolean("confirm_now"));
                room.setStandardNumberOfPeople(rs.getLong("STANDARD_NUMBER_OF_PEOPLE"));

                // Add the RoomsEntity object to the list
                roomsList.add(room);
            }
        } catch (SQLException e) {
            log.error("Error retrieving rooms by hotel ID: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return roomsList; // Return the list of rooms, which may be empty if none are found
    }


    public List<RoomsEntity> getRoomByHotelIdGuest(RoomRequestVerTwo filterParams) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("GET_ROOM_BY_HOTEL_ID_GUEST");
        List<RoomsEntity> roomsList = new ArrayList<>();

        try {
            ps = preparedStatement(sql);

            // Set parameters using positional placeholders
            int paramIndex = 0; // Start with the first parameter index

            // Start date with COALESCE
            ps.setDate(++paramIndex, filterParams.getDateFrom() != null ?
                    new java.sql.Date(filterParams.getDateFrom().getTime()) : null);

            // End date with COALESCE
            ps.setDate(++paramIndex, filterParams.getDateTo() != null ?
                    new java.sql.Date(filterParams.getDateTo().getTime()) : null);
            Integer numberRoom = filterParams.getNumberRoom();
            // Number of rooms with COALESCE
            if (numberRoom != null) {
                ps.setObject(++paramIndex, numberRoom, Types.INTEGER);
            } else {
                ps.setNull(++paramIndex, Types.INTEGER);
            }
            ps.setDate(++paramIndex, filterParams.getDateTo() != null ?
                    new java.sql.Date(filterParams.getDateTo().getTime()) : null);
            Integer numberMaxPeople = filterParams.getNumberPeople();

            ps.setDate(++paramIndex, filterParams.getDateFrom() != null ?
                    new java.sql.Date(filterParams.getDateFrom().getTime()) : null);

            ps.setDate(++paramIndex, filterParams.getDateFrom() != null ?
                    new java.sql.Date(filterParams.getDateFrom().getTime()) : null);
            // Max people stay with COALESCE
            if (numberMaxPeople != null) {
                ps.setObject(++paramIndex, numberMaxPeople, Types.INTEGER);
            } else {
                ps.setNull(++paramIndex, Types.INTEGER);
            }

            ps.setLong(++paramIndex,filterParams.getId());
            // Limit

            // Execute the query and retrieve the result set
            rs = ps.executeQuery();

            // Iterate through the result set
            while (rs.next()) {
                // Create a RoomsEntity object for each row and populate it with data from the result set
                RoomsEntity room = new RoomsEntity();
                room.setId(rs.getLong("ID"));
                room.setName(rs.getString("ROOM_NAME"));
                room.setRoomCount(rs.getInt("ROOM_COUNT"));
                room.setRoomSize(rs.getInt("ROOM_SIZE"));
                room.setRoomTypeId(rs.getInt("ROOM_TYPE_ID"));
                room.setMinPrice(rs.getBigDecimal("MIN_PRICE"));
                room.setDefaultPrice(rs.getBigDecimal("DEFAULT_PRICE"));
                room.setWeekPrice(rs.getBigDecimal("WEEK_PRICE"));
                room.setMonthPrice(rs.getBigDecimal("MONTH_PRICE"));
                room.setAdditionalAdultFee(rs.getBigDecimal("ADDITIONAL_ADULT_FEE"));
                room.setAdditionalChildFee(rs.getBigDecimal("ADDITIONAL_CHILD_FEE"));
                room.setMaxPeopleStay(rs.getInt("MAX_PEOPLE_STAY"));
                room.setStatus(rs.getBoolean("STATUS"));
                room.setHotelId(rs.getInt("HOTEL_ID"));
                room.setPriceIncludesBreakfast(rs.getBoolean("PRICE_INCLUDES_BREAKFAST"));
                room.setComfirmationWithinMinute(rs.getBoolean("confirmation_within_30_minutes"));
                room.setSurchargeForAdultChild(rs.getBoolean("surcharge_for_adults_children"));
                room.setComfirmNow(rs.getBoolean("confirm_now"));
                room.setStandardNumberOfPeople(rs.getLong("STANDARD_NUMBER_OF_PEOPLE"));

                // Add the RoomsEntity object to the list
                roomsList.add(room);
            }
        } catch (SQLException e) {
            log.error("Error retrieving rooms by hotel ID: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return roomsList; // Return the list of rooms, which may be empty if none are found
    }




    public List<String> getAllServiceByRoomId(long roomId) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        List<String> serviceList = new ArrayList<>();

        try {
            // Retrieve the SQL query from the XML configuration
            String sql = sqlLoader.getSql("GET_SERVICE_ROOM");

            // Prepare the statement
            ps = preparedStatement(sql);

            // Set the parameter
            ps.setLong(1, roomId);

            // Execute the query
            rs = ps.executeQuery();

            // Process the result set
            while (rs.next()) {
                String hotelServiceName = rs.getString("ROOM_SERVICE_NAME");
                serviceList.add(hotelServiceName);
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            // Consider logging the exception or handling it in a way that fits your application
            return null; // Or return an empty list if you prefer
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return serviceList;
    }


    public List<BedRoomInfo> getBedRoomInfoByRoomId(long roomId) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        List<BedRoomInfo> bedRoomInfoList = new ArrayList<>();

        try {
            // Retrieve the SQL query from the XML configuration
            String sql = sqlLoader.getSql("GET_BED_IN_ROOM");

            // Create the prepared statement with parameters
            ps = preparedStatement(sql);
            ps.setLong(1, roomId);

            // Execute the query and retrieve the result set
            rs = ps.executeQuery();

            // Process the result set
            while (rs.next()) {
                // Create a new BedRoomInfo for each row in the result set
                BedRoomInfo bedRoomInfo = new BedRoomInfo();
                bedRoomInfo.setBedTypeName(rs.getString("BED_TYPE_NAME"));
                bedRoomInfo.setCountBed(rs.getInt("COUNT_BED"));
                bedRoomInfo.setBedTypeId(rs.getLong("BED_TYPE_ID"));

                // Add the BedRoomInfo to the list
                bedRoomInfoList.add(bedRoomInfo);
            }
        } catch (SQLException e) {
            log.error("Error retrieving bed information by room ID: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return bedRoomInfoList;
    }


    public List<String> getImage(long roomId) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        List<String> roomImages = new ArrayList<>();

        try {
            // Retrieve the SQL query from the XML configuration
            String sql = sqlLoader.getSql("GET_ROOM_IMAGE");

            // Create the prepared statement with parameters
            ps = preparedStatement(sql);
            ps.setLong(1, roomId);

            // Execute the query and retrieve the result set
            rs = ps.executeQuery();

            // Process the result set
            while (rs.next()) {
                String imageUrl = rs.getString("image_url");
                roomImages.add(imageUrl);
            }
        } catch (SQLException e) {
            log.error("Error retrieving room images by room ID: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return roomImages;
    }

    public void updateRoomAvailableByRoomId(int numberRoomAvailable,long roomId,  Date dateApply) {
        ResultSet rs = null;
        PreparedStatement ps = null;
            // Retrieve the SQL query from the XML configuration
            String sql = sqlLoader.getSql("UPDATE_ROOM_AVAILABLE_BY_ROOM_ID");

            // Prepare the update statement
            try {
                ps = preparedStatement(sql);
                // Set parameters in the prepared statement
                ps.setInt(1, numberRoomAvailable);
                ps.setLong(2, roomId);
                ps.setDate(3,  new java.sql.Date(dateApply.getTime()));

                // Execute the update
                int rowsAffected = ps.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Room availability updated successfully.");
                } else {
                    System.out.println("No rows were updated. Room ID may not exist.");
                }

            } catch (SQLException e) {
                log.error("Error updating room availability by room ID: " + e.getMessage(), e);
            } finally {
                // Close the result set and prepared statement in the finally block
                closeResultSet(rs);
                closePS(ps);
            }
    }

    public List<RoomAvailabilityResponse> getRoomAvailableByRoomId(long roomId, Date startDate, Date endDate) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        List<RoomAvailabilityResponse> roomAvailabilityResponses = new ArrayList<>();

        try {
            // Retrieve the SQL query from the XML configuration
            String sql = sqlLoader.getSql("GET_ROOM_AVAILABLE_BY_ROOM_ID");

            // Create the prepared statement with parameters
            ps = preparedStatement(sql);
            ps.setLong(1, roomId);
            ps.setDate(2, new java.sql.Date(startDate.getTime()));
            ps.setDate(3, new java.sql.Date(endDate.getTime()));

            // Execute the query and retrieve the result set
            rs = ps.executeQuery();

            // Process the result set
            while (rs.next()) {
                long id = rs.getLong("ID");
                int numberRoomAvailable = rs.getInt("NUMBER_ROOM_AVAILABLE");
                Date dateApply = rs.getDate("DATE_APPLY");

                RoomAvailabilityResponse roomAvailability = new RoomAvailabilityResponse(id, numberRoomAvailable, dateApply);
                roomAvailabilityResponses.add(roomAvailability);
            }
        } catch (SQLException e) {
            log.error("Error retrieving room availability by room ID: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return roomAvailabilityResponses;
    }

    public RoomAvailabilityResponse getRoomAvailableByRoomIdAndDateApply(long roomId, Date dateApply) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        RoomAvailabilityResponse roomAvailability = null;

        try {
            // Retrieve the SQL query from the XML configuration
            String sql = sqlLoader.getSql("GET_ROOM_AVAILABLE_BY_ROOM_ID_AND_DATE_APPLY");

            // Create the prepared statement with parameters
            ps = preparedStatement(sql);
            ps.setLong(1, roomId);
            ps.setDate(2, new java.sql.Date(dateApply.getTime()));

            // Execute the query and retrieve the result set
            rs = ps.executeQuery();

            // Process the result set
            if (rs.next()) {
                long id = rs.getLong("ID");
                int numberRoomAvailable = rs.getInt("NUMBER_ROOM_AVAILABLE");
                Date dateApplyResult = rs.getDate("DATE_APPLY");

                roomAvailability = new RoomAvailabilityResponse(id, numberRoomAvailable, dateApplyResult);
            }
        } catch (SQLException e) {
            log.error("Error retrieving room availability by room ID and date apply: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return roomAvailability;
    }


    public RoomsEntity checkRoomAvailableByRoomId(RoomRequestVerTwo roomRequestVerTwo) {
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            // Retrieve the SQL query from the XML configuration
            String sql = sqlLoader.getSql("GET_ROOM_BY_DATE_AVAILABLE");
            int paramIndex = 1;
            // Create the prepared statement with parameters
            ps = preparedStatement(sql);

            ps.setLong(paramIndex++, roomRequestVerTwo.getId());
            ps.setDate(paramIndex++, roomRequestVerTwo.getDateFrom() != null ? new java.sql.Date(roomRequestVerTwo.getDateFrom().getTime()) : null);

            ps.setLong(paramIndex++, roomRequestVerTwo.getId());
            ps.setDate(paramIndex++, roomRequestVerTwo.getDateFrom() != null ? new java.sql.Date(roomRequestVerTwo.getDateFrom().getTime()) : null);

            ps.setLong(paramIndex++, roomRequestVerTwo.getId());
            ps.setDate(paramIndex++, roomRequestVerTwo.getDateFrom() != null ? new java.sql.Date(roomRequestVerTwo.getDateFrom().getTime()) : null);

            ps.setInt(paramIndex++, roomRequestVerTwo.getNumberPeople() != null ? roomRequestVerTwo.getNumberPeople() : null);

            ps.setDate(paramIndex++, roomRequestVerTwo.getDateFrom() != null ? new java.sql.Date(roomRequestVerTwo.getDateFrom().getTime()) : null);
            ps.setDate(paramIndex++, roomRequestVerTwo.getDateTo() != null ? new java.sql.Date(roomRequestVerTwo.getDateTo().getTime()) : null);

            ps.setInt(paramIndex++, roomRequestVerTwo.getNumberRoom() != null ? roomRequestVerTwo.getNumberRoom() : null);
            ps.setLong(paramIndex++, roomRequestVerTwo.getId());
            ps.setLong(paramIndex++, roomRequestVerTwo.getId());

            ps.setDate(paramIndex++, roomRequestVerTwo.getDateFrom() != null ? new java.sql.Date(roomRequestVerTwo.getDateFrom().getTime()) : null);
            ps.setDate(paramIndex++, roomRequestVerTwo.getDateTo() != null ? new java.sql.Date(roomRequestVerTwo.getDateTo().getTime()) : null);

            ps.setDate(paramIndex++, roomRequestVerTwo.getDateFrom() != null ? new java.sql.Date(roomRequestVerTwo.getDateFrom().getTime()) : null);
            ps.setDate(paramIndex++, roomRequestVerTwo.getDateTo() != null ? new java.sql.Date(roomRequestVerTwo.getDateTo().getTime()) : null);
            ps.setDate(paramIndex++, roomRequestVerTwo.getDateFrom() != null ? new java.sql.Date(roomRequestVerTwo.getDateFrom().getTime()) : null);
            ps.setDate(paramIndex++, roomRequestVerTwo.getDateTo() != null ? new java.sql.Date(roomRequestVerTwo.getDateTo().getTime()) : null);

            ps.setInt(paramIndex++, roomRequestVerTwo.getNumberRoom() != null ? roomRequestVerTwo.getNumberRoom() : null);
            ps.setLong(paramIndex++, roomRequestVerTwo.getId());

            ps.setObject(paramIndex++, roomRequestVerTwo.getDateTo() != null ? new java.sql.Date(roomRequestVerTwo.getDateTo().getTime()) : null);
            ps.setObject(paramIndex, roomRequestVerTwo.getDateFrom() != null ? new java.sql.Date(roomRequestVerTwo.getDateFrom().getTime()) : null);

            // Execute the query and retrieve the result set
            rs = ps.executeQuery();
            if (rs.next()) {
                // Create a RoomsEntity object and populate it with data from the result set
                RoomsEntity room = new RoomsEntity();
                room.setId(rs.getLong("ID"));
                room.setName(rs.getString("ROOM_NAME"));
                room.setRoomCount(rs.getInt("ROOM_COUNT"));
                room.setRoomSize(rs.getInt("ROOM_SIZE"));
                room.setRoomTypeId(rs.getInt("ROOM_TYPE_ID"));
                room.setMinPrice(rs.getBigDecimal("MIN_PRICE"));
                room.setDefaultPrice(rs.getBigDecimal("DEFAULT_PRICE"));
                room.setWeekPrice(rs.getBigDecimal("WEEK_PRICE"));
                room.setMonthPrice(rs.getBigDecimal("MONTH_PRICE"));
                room.setAdditionalAdultFee(rs.getBigDecimal("ADDITIONAL_ADULT_FEE"));
                room.setAdditionalChildFee(rs.getBigDecimal("ADDITIONAL_CHILD_FEE"));
                room.setMaxPeopleStay(rs.getInt("MAX_PEOPLE_STAY"));
                room.setStatus(rs.getBoolean("STATUS"));
                room.setHotelId(rs.getInt("HOTEL_ID"));
                room.setRoomCode(rs.getString("ROOM_CODE"));
                room.setPriceIncludesBreakfast(rs.getBoolean("PRICE_INCLUDES_BREAKFAST"));
                room.setComfirmationWithinMinute(rs.getBoolean("confirmation_within_30_minutes"));
                room.setSurchargeForAdultChild(rs.getBoolean("surcharge_for_adults_children"));
                room.setComfirmNow(rs.getBoolean("confirm_now"));
                return room;
            }
        } catch (SQLException e) {
            log.error("Error checking room availability by room ID: " + e.getMessage(), e);
            return null;
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
        return null;
    }




    public void updateRooms(UpdateRoomRequest request) throws Exception {
        PreparedStatement ps = null;

        String sql = sqlLoader.getSql("UPDATE_ROOM");
        List<Object> params = new ArrayList<>();
        params.add(request.getRoomName());
        params.add(request.getRoomCount());
        params.add(request.getRoomSize());
        params.add(request.getRoomTypeId());
        params.add(request.getMinPrice());
        params.add(request.getDefaultPrice());
        params.add(request.getWeekPrice());
        params.add(request.getMonthPrice());
        params.add(request.getAdditionalAdultFee());
        params.add(request.getAdditionalChildFee());
        params.add(request.getMaxPeopleStay());
        params.add(request.getRoomCode());
        params.add(request.getPriceIncludesBreakfast());
        params.add(request.getConfirmationWithinMinute());
        params.add(request.getSurchargeForAdultChild());
        params.add(request.getConfirmNow());
        params.add(request.getStandardNumberOfPeople());
        params.add(request.getRoomId());
        try {
            ps = preparedStatement(sql, params);
            ps.executeUpdate();
        } finally {
            closePS(ps);
        }
    }

    public void deleteRoomService(Long roomId) throws Exception {
        PreparedStatement ps = null;

        String sql = sqlLoader.getSql("DELETE_ROOM_SERVICE");

        try {
            ps = preparedStatement(sql);
            int idx = 0;
            ps.setLong(++idx, roomId);
            ps.executeUpdate();
        } finally {
            closePS(ps);
        }
    }

    public void deleteBedRoom(Long roomId) throws Exception {
        PreparedStatement ps = null;

        String sql = sqlLoader.getSql("DELETE_BED_ROOM");

        try {
            ps = preparedStatement(sql);
            int idx = 0;
            ps.setLong(++idx, roomId);
            ps.executeUpdate();
        } finally {
            closePS(ps);
        }
    }

    public Boolean checkRoomCode(Long roomId, String roomCode, boolean updateRoom, Long userId) throws Exception {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("CHECK_ROOM_CODE");
        if (updateRoom) {
            sql = sql.replace("#CONDITION#", sqlLoader.getSql("CONDITION_UPDATE_ROOM_CODE"));
        } else {
            sql = sql.replace("#CONDITION#", sqlLoader.getSql("CONDITION_CREATE_ROOM_CODE"));
        }
        try {
            ps = preparedStatement(sql);
            int idx = 0;
            if (updateRoom) {
                ps.setLong(++idx, roomId);
                ps.setString(++idx, roomCode);
                ps.setLong(++idx, roomId);
            } else {
                ps.setLong(++idx, userId);
                ps.setString(++idx, roomCode);
            }
            rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
        return false;
    }

    public RoomResponeseVerTwo getRoomDetailById(long id) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("GET_ROOM_DETAIL_BY_ID");

        try {
            // Create a list to hold the query parameters
            List<Object> params = new ArrayList<>();
            params.add(id);

            // Create the prepared statement with parameters
            ps = preparedStatement(sql, params);

            // Execute the query and retrieve the result set
            rs = ps.executeQuery();

            // Check if there's a result
            if (rs.next()) {
                // Create a RoomsEntity object and populate it with data from the result set
                RoomResponeseVerTwo room = new RoomResponeseVerTwo();
                room.setId(rs.getLong("ID"));
                room.setName(rs.getString("ROOM_NAME"));
                room.setRoomCount(rs.getInt("ROOM_COUNT"));
                room.setRoomSize(rs.getInt("ROOM_SIZE"));
                room.setRoomTypeId(rs.getLong("ROOM_TYPE_ID"));
                room.setMinPrice(rs.getBigDecimal("MIN_PRICE"));
                room.setDefaultPrice(rs.getBigDecimal("DEFAULT_PRICE"));
                room.setWeekPrice(rs.getBigDecimal("WEEK_PRICE"));
                room.setMonthPrice(rs.getBigDecimal("MONTH_PRICE"));
                room.setAdditionalAdultFee(rs.getBigDecimal("ADDITIONAL_ADULT_FEE"));
                room.setAdditionalChildFee(rs.getBigDecimal("ADDITIONAL_CHILD_FEE"));
                room.setMaxPeopleStay(rs.getInt("MAX_PEOPLE_STAY"));
                room.setStatus(rs.getBoolean("STATUS"));
                room.setHotelId(rs.getInt("HOTEL_ID"));
                room.setPriceIncludesBreakfast(rs.getBoolean("PRICE_INCLUDES_BREAKFAST"));
                room.setComfirmationWithinMinute(rs.getBoolean("confirmation_within_30_minutes"));
                room.setSurchargeForAdultChild(rs.getBoolean("surcharge_for_adults_children"));
                room.setComfirmNow(rs.getBoolean("confirm_now"));
                room.setStandardNumberOfPeople(rs.getLong("STANDARD_NUMBER_OF_PEOPLE"));
                room.setRoomCode(rs.getString("ROOM_CODE"));
                return room;
            }

        } catch (SQLException e) {
            log.error("Error retrieving room by ID: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return null; // Return null if no room with the provided ID was found
    }

    public List<Long> getListServiceByRoomId(long roomId) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        List<Long> serviceList = new ArrayList<>();

        try {
            // Retrieve the SQL query from the XML configuration
            String sql = sqlLoader.getSql("GET_ROOM_SERVICE_BY_ID");

            // Prepare the statement
            ps = preparedStatement(sql);

            // Set the parameter
            ps.setLong(1, roomId);

            // Execute the query
            rs = ps.executeQuery();

            // Process the result set
            while (rs.next()) {
                serviceList.add(rs.getLong("ID"));
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            // Consider logging the exception or handling it in a way that fits your application
            return null; // Or return an empty list if you prefer
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return serviceList;
    }

    public int getTotalRoomBookingStaff(Long userId, SearchRoomRequest request) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int totalRecords = 0;
        try {
            //ＳＱＬ文の取得
            String sql = sqlLoader.getSql("COUNT_ROOM_OUTPUT_BOOKING_STAFF");
            sql = sql.replace(MasterDataConstants.EXTENDS_CONDITION,
                    HandlerUtils.createFullTextSearchQuery(listFieldSearch, request.getSearchParams(), true));
            // excute query
            ps = preparedStatement(sql);

            int idx = 0;
            ps.setLong(++idx, userId);

            //ＳＱＬ実行
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

    public List<RoomResponse> searchRoomManagementsBookingStaff(Long userId, SearchRoomRequest request) throws Exception {
        List<RoomResponse> responseList = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement ps = null;

        String sortBy = setSortBySqlColumnName(request.getBaseSearchPagingDTO().getSortBy());
        request.getBaseSearchPagingDTO().setSortBy(sortBy);

        String sql = sqlLoader.getSql("SEARCH_ROOM_USER_MANAGEMENT_BOOKING_STAFF");

        sql = sql.replace(MasterDataConstants.EXTENDS_CONDITION,
                HandlerUtils.createFullTextSearchQuery(listFieldSearch, request.getSearchParams(), true));
        StringBuilder pagingQuery = HandlerUtils.appendSortQuery(request.getBaseSearchPagingDTO());
        sql = sql.replace(MasterDataConstants.EXTENDS_PAGING, pagingQuery.toString());

        try {
            ps = preparedStatement(sql);
            int idx = 0;
            ps.setLong(++idx, userId);
            rs = ps.executeQuery();
            while (rs.next()) {
                RoomResponse response = new RoomResponse();
                response.setId(rs.getLong("ID"));
                response.setName(rs.getString("ROOM_NAME"));
                response.setRoomType(rs.getString("ROOM_TYPE_NAME"));
                response.setRoomCount(rs.getInt("ROOM_COUNT"));
                response.setRoomSize(rs.getInt("ROOM_SIZE"));
                response.setMaxPeopleStay(rs.getInt("MAX_PEOPLE_STAY"));
                response.setDefaultPrice(rs.getBigDecimal("DEFAULT_PRICE"));
                response.setStatus(rs.getBoolean("STATUS"));
                responseList.add(response);
            }
        } catch (SQLException e) {
            log.info(e.getMessage(), e);
            return null;
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return responseList;
    }

    public List<RoomResponse> searchListRoomManagement(Long userId) throws Exception {
        List<RoomResponse> responseList = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement ps = null;

        String sql = sqlLoader.getSql("SELECT_ROOM_MANAGEMENT");

        try {
            ps = preparedStatement(sql);
            int idx = 0;
            ps.setLong(++idx, userId);
            rs = ps.executeQuery();
            while (rs.next()) {
                RoomResponse response = new RoomResponse();
                response.setId(rs.getLong("ID"));
                response.setName(rs.getString("ROOM_NAME"));
                response.setRoomCode(rs.getString("ROOM_CODE"));
                responseList.add(response);
            }
        } catch (SQLException e) {
            log.info(e.getMessage(), e);
            return null;
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return responseList;
    }

    public void insertRoomImage(Long roomId, List<String> imageUrls) throws SQLException {
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("INSERT_ROOM_IMAGE");
        try {
            ps = preparedStatement(sql);
            for (String url : imageUrls) {
                int idx = 0;
                ps.setString(++idx, url);
                ps.setLong(++idx, roomId);
                ps.addBatch();
            }
            ps.executeBatch();
        } finally {
            closePS(ps);
        }
    }

    public void deleteRoomImage(Long hotelId) throws Exception {
        PreparedStatement ps = null;

        String sql = sqlLoader.getSql("DELETE_ROOM_IMAGE");

        try {
            ps = preparedStatement(sql);
            int idx = 0;
            ps.setLong(++idx, hotelId);
            ps.executeUpdate();
        } finally {
            closePS(ps);
        }
    }

    public void deleteListImage(Long hotelId, List<String> imageUrls) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = sqlLoader.getSql("DELETE_IMAGE_ROOM_UPDATE");
        String temp = "";
        if(!imageUrls.isEmpty()) {
            temp += " and image_url not in ( ";
        }
        for(int i = 0; i < imageUrls.size(); i++) {
            if(i == imageUrls.size() - 1) {
                temp += "'" + imageUrls.get(i) + "' )";
            } else {
                temp +=  "'" + imageUrls.get(i) + "' , ";
            }
        }
        sql = sql.replace("#EXTEND_CONDITION#", temp);
        List<Object> params = new ArrayList<>();
        params.add(hotelId);
        try {
            ps = preparedStatement(sql, params);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
    }

    public List<String> getListImageOldDelete(Long hotelId, List<String> imageUrls) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<String> data = new ArrayList<>();
        String sql = sqlLoader.getSql("GET_LIST_IMAGE_ROOM_DELETE");
        String temp = "";
        if(!imageUrls.isEmpty()) {
            temp += " and image_url not in ( ";
        }
        for(int i = 0; i < imageUrls.size(); i++) {
            if(i == imageUrls.size() - 1) {
                temp += "'" + imageUrls.get(i) + "' )";
            } else {
                temp +=  "'" + imageUrls.get(i) + "' , ";
            }
        }
        sql = sql.replace("#EXTEND_CONDITION#", temp);
        List<Object> params = new ArrayList<>();
        params.add(hotelId);
        try {
            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();
            while (rs.next()) {
                data.add(rs.getString(1));
            }
            return data;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
    }

    public Integer getNumberRoomAvailable(Integer roomId, Date date) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = sqlLoader.getSql("GET_NUMBER_ROOM_AVAILABLE");

        List<Object> params = new ArrayList<>();
        params.add(roomId);
        params.add(date);
        try {
            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
        return null;
    }
}
