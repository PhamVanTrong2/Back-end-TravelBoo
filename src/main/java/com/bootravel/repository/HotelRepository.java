package com.bootravel.repository;

import com.bootravel.common.CommonRepository;
import com.bootravel.common.constant.MasterDataConstants;
import com.bootravel.entity.HotelImagesEntity;
import com.bootravel.entity.HotelsEntity;
import com.bootravel.entity.RoomsEntity;
import com.bootravel.exception.BadRequestAlertException;
import com.bootravel.payload.requests.SearchHotelRequest;
import com.bootravel.payload.requests.UpdateHotelRequest;
import com.bootravel.payload.requests.commonRequests.HotelFilterRequest;
import com.bootravel.payload.responses.*;
import com.bootravel.service.PromotionService;
import com.bootravel.utils.HandlerUtils;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.*;

@Repository
@Slf4j

public class HotelRepository extends CommonRepository {
    private static final String ENTITY_NAME = "HotelService";
    @Autowired
    private RoomsRepository roomsRepository;

    @Autowired
    private PromotionService promotionService;

    private List<String> listFieldSearch = Arrays.asList("HOTEL_NAME", "TAX_CODE");

    public HotelRepository() throws ParserConfigurationException, IOException, SAXException {
        super();
    }

    @Override
    protected String getFileKey() {
        return "/sql/sqlHotelRepository.xml";
    }

    public void insertHotel(HotelsEntity hotelsEntity) throws SQLException {
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("INSERT_HOTEL");
        List<Object> params = new ArrayList<>();
        params.add(hotelsEntity.getId());
        params.add(hotelsEntity.getName());
        params.add(hotelsEntity.getStar());
        params.add(hotelsEntity.getLattitude());
        params.add(hotelsEntity.getLongtitude());
        params.add(hotelsEntity.getDescription());
        params.add(hotelsEntity.getNote());
        params.add(hotelsEntity.getAddressId());
        params.add(hotelsEntity.getTaxCode());
        params.add(hotelsEntity.getHotelPhoneNumber());
        try {
            ps = preparedStatement(sql, params);
            ps.executeUpdate();
        } finally {
            closePS(ps);
        }
    }
    public void insertHotelImage(Long hotelId, List<String> imageUrls) throws SQLException {
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("INSERT_HOTEL_IMAGE");
        try {
            ps = preparedStatement(sql);
            for (String url : imageUrls) {
                int idx = 0;
                ps.setString(++idx, url);
                ps.setLong(++idx, hotelId);
                ps.addBatch();
            }
            ps.executeBatch();
        } finally {
            closePS(ps);
        }
    }
    public void updateEmployeeOfHotel(Long userId, Long hotelId) throws SQLException {
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("UPDATE_EMPLOYEE_OF_HOTEL");
        List<Object> params = new ArrayList<>();
        params.add(hotelId);
        params.add(userId);
        try {
            ps = preparedStatement(sql, params);
            ps.executeUpdate();
        } finally {
            closePS(ps);
        }
    }

    public void insertHotelService(Long hotelId, List<Long> listService) throws SQLException {
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("INSERT_HOTEL_SERVICE");
        try {
            ps = preparedStatement(sql);
            for (Long serviceId : listService) {
                int idx = 0;
                ps.setLong(++idx, hotelId);
                ps.setLong(++idx, serviceId);
                ps.addBatch();
            }
            ps.executeBatch();
        } finally {
            closePS(ps);
        }
    }


    public boolean checkTaxCode(String taxCode) throws Exception {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("CHECK_TAX_CODE");
        try {
            ps = preparedStatement(sql);
            ps.setString(1, taxCode);
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

    public boolean checkPhoneNumber(String phoneNumber) throws Exception {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("CHECK_PHONE_NUMBER");
        try {
            ps = preparedStatement(sql);
            ps.setString(1, phoneNumber);
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

    public Integer getTotalHotelOutput(SearchHotelRequest request, Long userId) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int totalRecords = 0;
        try {
            //ＳＱＬ文の取得
            String sql = sqlLoader.getSql("COUNT_HOTELS_OUTPUT");
            //sql = sql.replace(MasterDataConstants.EXTENDS_CONDITION, appendSqlSearchHotelCondition(request).toString());
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

    public StringBuilder appendSqlSearchHotelCondition(SearchHotelRequest searchConditon) {
        StringBuilder sqlBuilder = new StringBuilder();
        // with 部署選択 condition
        if (StringUtils.isNotEmpty(searchConditon.getSearchParams())) {
            String[] condition = searchConditon.getSearchParams().split(MasterDataConstants.COMMA);
            sqlBuilder.append(MasterDataConstants.SQL_AND).append(HandlerUtils.appendInCondition("HOTEL_NAME", condition))
                    .append(MasterDataConstants.SQL_OR)
                    .append(HandlerUtils.appendInCondition("TAX_CODE", condition))
            ;
        }
        return sqlBuilder;
    }

    public List<SearchHotelResponse> searchHotel(SearchHotelRequest request, Long userId) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        List<SearchHotelResponse> listHotels = new ArrayList<>();
        String sortBy = setSortBySqlColumnName(request.getSearchPaging().getSortBy());
        request.getSearchPaging().setSortBy(sortBy);

        String sql = sqlLoader.getSql("SEARCH_HOTEL_FOR_MANAGER");

        //sql = sql.replace(MasterDataConstants.EXTENDS_CONDITION, appendSqlSearchHotelCondition(request).toString());
        sql = sql.replace(MasterDataConstants.EXTENDS_CONDITION,
                HandlerUtils.createFullTextSearchQuery(listFieldSearch, request.getSearchParams(), true));
        StringBuilder pagingQuery = HandlerUtils.appendSortQuery(request.getSearchPaging());
        sql = sql.replace(MasterDataConstants.EXTENDS_PAGING, pagingQuery.toString());

        try {
            ps = preparedStatement(sql);
            int idx = 0;
            ps.setLong(++idx, userId);
            rs = ps.executeQuery();
            while (rs.next()) {
                SearchHotelResponse response = new SearchHotelResponse();
                response.setId(rs.getLong("ID"));
                response.setName(rs.getString("HOTEL_NAME"));
                response.setStar(rs.getBigDecimal("HOTEL_STAR"));
                response.setStatus(rs.getBoolean("STATUS"));
                response.setTaxCode(rs.getString("TAX_CODE"));
                response.setAddress(rs.getString("ADDRESS"));
                response.setBusinessOwnerName(rs.getString("BUSINESS_OWNER_NAME"));
                listHotels.add(response);
            }
        } catch (SQLException e) {
            log.info(e.getMessage(), e);
            return null;
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
        return listHotels;

    }


    public String setSortBySqlColumnName(String orderBy) {
        switch (orderBy) {
            case "hotelName":
                return "HOTEL_NAME";
            case "id":
                return "ID";
            case "hotelStar":
                return "HOTEL_STAR";
            default:
                return "";
        }
    }

    public GetHotelResponse getHotelById(Long id) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = sqlLoader.getSql("GET_HOTEL_BY_ID");
        List<Object> params = new ArrayList<>();
        params.add(id);
        try {
            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();
            if (rs.next()) {
                GetHotelResponse hotel = new GetHotelResponse();
                hotel.setId(rs.getLong("ID"));
                hotel.setName(rs.getString("HOTEL_NAME"));
                hotel.setStar(rs.getInt("HOTEL_STAR"));
                hotel.setLattitude(rs.getBigDecimal("LATTITUDE"));
                hotel.setLongtitude(rs.getBigDecimal("LONGTITUDE"));
                hotel.setDescription(rs.getString("DESCRIPTION"));
                hotel.setNote(rs.getString("NOTE"));
                hotel.setTaxCode(rs.getString("TAX_CODE"));
                hotel.setStatus(rs.getBoolean("STATUS"));
                hotel.setBoId(rs.getLong("USER_ID"));
                hotel.setAddress1(rs.getString("ADDRESS1"));
                hotel.setAddress2(rs.getString("ADDRESS2"));
                hotel.setWardId(rs.getLong("WARD_Id"));
                hotel.setDistrictId(rs.getLong("DISTRICT_ID"));
                hotel.setProvinceId(rs.getLong("PROVINCE_ID"));
                hotel.setBoName(rs.getString("USER_NAME"));
                hotel.setPhoneNumber(rs.getString("HOTEL_PHONE_NUMBER"));
                if (StringUtils.isNotEmpty(rs.getString("LIST_SERVICE"))){
                    hotel.setListHotelService(Arrays.asList(rs.getString("LIST_SERVICE").split(",")));
                }
                return hotel;
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

    public void updateStatus(Long id, Boolean status) {
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("UPDATE_STATUS_HOTEL");
        List<Object> params = new ArrayList<>();
        params.add(status);
        params.add(id);
        try {
            ps = preparedStatement(sql, params);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closePS(ps);
        }
    }

    public List<GetHotelResponseVerThree> filterHotel(HotelFilterRequest filterParams, String searchProvince, String searchDistrict) throws Exception {
        ResultSet rs = null;
        PreparedStatement ps = null;
        List<GetHotelResponseVerThree> filteredHotels = new ArrayList<>();

        // Retrieve the SQL query from the XML configuration
        String sql = sqlLoader.getSql("SEARCH_HOTEL");

        if (StringUtils.isNotEmpty(searchDistrict) || StringUtils.isNotEmpty(searchProvince)) {
            if (StringUtils.isNotEmpty(searchProvince)) {
                sql = sql.replace("#CONDITION", " BOO_TRAVEL.UNACCENT(P.PROVINCE_NAME) ILIKE BOO_TRAVEL.UNACCENT('"
                        + searchProvince + "')");
                filterParams.setSearch(searchProvince);
            } else {
                sql = sql.replace("#CONDITION", " BOO_TRAVEL.UNACCENT(D.DISTRICT_NAME) ILIKE BOO_TRAVEL.UNACCENT('"
                        + searchDistrict + "')");
                filterParams.setSearch(searchDistrict);

            }
        } else {
            sql = sql.replace("#CONDITION", "");
        }
        sql = sql.replace("#ORDER#", filterParams.getSortBySql());


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

            // Limit
            ps.setInt(++paramIndex, filterParams.getLimit());

            // Offset
            ps.setInt(++paramIndex, filterParams.getOffset());

            rs = ps.executeQuery();

            while (rs.next()) {
                GetHotelResponseVerThree hotelData = new GetHotelResponseVerThree();
                long hotelId = rs.getLong("HOTEL_ID");
                var rooms = roomsRepository.getRoomByHotelId(Math.toIntExact(hotelId));

                if (filterParams.getRangePrice() != null) {
                    Integer minPrice = filterParams.getRangePrice().get(0);
                    Integer maxPrice = filterParams.getRangePrice().get(1);

                    // Find the first room with a non-null DEFAULT_PRICE within the specified range
                    RoomsEntity roomWithDefaultPriceInRange = rooms
                            .stream()
                            .filter(room -> room.getDefaultPrice() != null)  // Ensure getDefaultPrice is not null
                            .filter(room -> {
                                BigDecimal defaultPriceBigDecimal = room.getDefaultPrice();
                                Integer defaultPriceInteger = defaultPriceBigDecimal.intValue();  // Convert BigDecimal to Integer

                                return defaultPriceInteger >= minPrice && defaultPriceInteger <= maxPrice;
                            })
                            .findFirst()
                            .orElse(null);

                    if (roomWithDefaultPriceInRange != null) {
                        // Set the default price of the found room to hotelData
                        hotelData.setDefaultPrice(BigDecimal.valueOf(roomWithDefaultPriceInRange.getDefaultPrice().intValue()));
                        hotelData.setId(rs.getLong("HOTEL_ID"));
                        hotelData.setName(rs.getString("HOTEL_NAME"));
                        hotelData.setStar(rs.getInt("HOTEL_STAR"));
                        hotelData.setLattitude(rs.getBigDecimal("LATTITUDE"));
                        hotelData.setLongtitude(rs.getBigDecimal("LONGTITUDE"));
                        hotelData.setProvinceName(rs.getString("PROVINCE_NAME"));
                        hotelData.setDescription(rs.getString("DESCRIPTION"));
                        hotelData.setNote(rs.getString("NOTE"));


                        if(hotelData.getId() != null){
                            var hotel = getHotelById(hotelData.getId());
                            if (hotel != null && hotel.getBoId() != null) {
                                hotelData.setBoId(hotel.getBoId());
                            } else {
                                hotelData.setBoId(null);
                            }
                            if(hotel != null && hotel.getBoName() !=null){
                                hotelData.setBoName(hotel.getBoName());
                            }else {
                                hotelData.setBoName(null);
                            }
                        }else {
                            hotelData.setBoId(null);
                            hotelData.setBoName(null);
                        }

                        hotelData.setTaxCode(rs.getString("TAX_CODE"));
                        hotelData.setStatus(rs.getBoolean("STATUS"));
                        hotelData.setAddress1(rs.getString("ADDRESS1"));
                        hotelData.setAddress2(rs.getString("ADDRESS2"));
                        hotelData.setWardName(rs.getString("WARD_NAME"));
                        hotelData.setDistrictName(rs.getString("DISTRICT_NAME"));
                        hotelData.setConfirmNow(rs.getBoolean("CONFRIM_NOW"));
                        hotelData.setIncludesBreakfast(rs.getBoolean("INCLUDES_BREAKFAST"));

                        Timestamp timestamp = rs.getTimestamp("BOOKED_AGO");
                        if (timestamp == null) {
                            hotelData.setBookedAgo(null);
                        }else{
                            Date date = new Date(timestamp.getTime());

                            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            outputFormat.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
                            String formattedDate = outputFormat.format(date);
                            hotelData.setBookedAgo(formattedDate);
                        }
                        // Set more fields as needed
                        String imageUrls = rs.getString("ALL_IMAGE_URLS");
                        if (imageUrls != null) {
                            List<String> imageUrlsList = Arrays.asList(imageUrls.split(", "));
                            hotelData.setListImages(imageUrlsList);
                        } else {
                            hotelData.setListImages(Collections.emptyList());
                        }
                        String hotelService = rs.getString("list_service");
                        if (hotelService != null) {
                            List<String> listService = Arrays.asList(hotelService.split(", "));
                            hotelData.setListService(listService);
                        }else {
                            hotelData.setListService(Collections.emptyList());
                        }
                        hotelData.setFinalPrice(rs.getBigDecimal("FINAL_PRICE"));
                        var promotion = promotionService.getPromotionById(40L);
                        PromotionDefaultResponse promotionDefaultResponse = new PromotionDefaultResponse();
                        promotionDefaultResponse.setTypePromotion(promotion.getData().getTypePromotion());
                        promotionDefaultResponse.setDiscountPercent(promotion.getData().getDiscountPercent());
                        promotionDefaultResponse.setPromotionCode(promotion.getData().getCode());
                        promotionDefaultResponse.setMaxDiscount(promotion.getData().getMaxDiscount());
                        hotelData.setPromotion(promotionDefaultResponse);
                        filteredHotels.add(hotelData);
                    } else {

                    }
                } else {
                    // Handle the case where no range is specified
                    RoomsEntity roomWithDefaultPrice = rooms
                            .stream()
                            .filter(room -> room.getDefaultPrice() != null)
                            .findFirst()
                            .orElse(null);

                    if (roomWithDefaultPrice != null) {
                        // Set the default price of the found room to hotelData
                        hotelData.setDefaultPrice(BigDecimal.valueOf(roomWithDefaultPrice.getDefaultPrice().intValue()));                    } else {
                    }
                    hotelData.setId(rs.getLong("HOTEL_ID"));
                    hotelData.setName(rs.getString("HOTEL_NAME"));
                    hotelData.setStar(rs.getInt("HOTEL_STAR"));
                    hotelData.setLattitude(rs.getBigDecimal("LATTITUDE"));
                    hotelData.setLongtitude(rs.getBigDecimal("LONGTITUDE"));
                    hotelData.setProvinceName(rs.getString("PROVINCE_NAME"));
                    hotelData.setDescription(rs.getString("DESCRIPTION"));
                    hotelData.setNote(rs.getString("NOTE"));


                    if(hotelData.getId() != null){
                        var hotel = getHotelById(hotelData.getId());
                        if (hotel != null && hotel.getBoId() != null) {
                            hotelData.setBoId(hotel.getBoId());
                        } else {
                            hotelData.setBoId(null);
                        }
                        if(hotel != null && hotel.getBoName() !=null){
                            hotelData.setBoName(hotel.getBoName());
                        }else {
                            hotelData.setBoName(null);
                        }
                    }else {
                        hotelData.setBoId(null);
                        hotelData.setBoName(null);
                    }

                    hotelData.setTaxCode(rs.getString("TAX_CODE"));
                    hotelData.setStatus(rs.getBoolean("STATUS"));
                    hotelData.setAddress1(rs.getString("ADDRESS1"));
                    hotelData.setAddress2(rs.getString("ADDRESS2"));
                    hotelData.setWardName(rs.getString("WARD_NAME"));
                    hotelData.setDistrictName(rs.getString("DISTRICT_NAME"));
                    hotelData.setConfirmNow(rs.getBoolean("CONFRIM_NOW"));
                    hotelData.setIncludesBreakfast(rs.getBoolean("INCLUDES_BREAKFAST"));

                    Timestamp timestamp = rs.getTimestamp("BOOKED_AGO");
                    if (timestamp == null) {
                        hotelData.setBookedAgo(null);
                    }else{
                        Date date = new Date(timestamp.getTime());

                        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        outputFormat.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
                        String formattedDate = outputFormat.format(date);
                        hotelData.setBookedAgo(formattedDate);
                    }
                    // Set more fields as needed
                    String imageUrls = rs.getString("ALL_IMAGE_URLS");
                    if (imageUrls != null) {
                        List<String> imageUrlsList = Arrays.asList(imageUrls.split(", "));
                        hotelData.setListImages(imageUrlsList);
                    } else {
                        hotelData.setListImages(Collections.emptyList());
                    }
                    String hotelService = rs.getString("list_service");
                    if (hotelService != null) {
                        List<String> listService = Arrays.asList(hotelService.split(", "));
                        hotelData.setListService(listService);
                    }else {
                        hotelData.setListService(Collections.emptyList());
                    }
                    hotelData.setFinalPrice(rs.getBigDecimal("FINAL_PRICE"));
                    var promotion = promotionService.getPromotionById(40L);
                    PromotionDefaultResponse promotionDefaultResponse = new PromotionDefaultResponse();
                    promotionDefaultResponse.setTypePromotion(promotion.getData().getTypePromotion());
                    promotionDefaultResponse.setDiscountPercent(promotion.getData().getDiscountPercent());
                    promotionDefaultResponse.setPromotionCode(promotion.getData().getCode());
                    promotionDefaultResponse.setMaxDiscount(promotion.getData().getMaxDiscount());
                    hotelData.setPromotion(promotionDefaultResponse);
                    filteredHotels.add(hotelData);
                }

            }
        } catch (Exception e) {
            throw new BadRequestAlertException("No data", ENTITY_NAME, "NOT_FOUND");
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return filteredHotels;
    }


    public List<String> getAllServiceByHotelId(long hotelId) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        List<String> serviceList = new ArrayList<>();

        try {
            // Retrieve the SQL query from the XML configuration
                String sql = sqlLoader.getSql("GET_SERVICE_HOTEL");

            // Replace the parameter placeholder in the SQL query
            sql = sql.replace("?", String.valueOf(hotelId));

            // Prepare the statement
            ps = preparedStatement(sql);

            // Execute the query
            rs = ps.executeQuery();

            // Process the result set
            while (rs.next()) {
                String hotelServiceName = rs.getString("HOTEL_SERVICE_NAME");
                serviceList.add(hotelServiceName);
            }
        } catch (SQLException e) {
            log.info(e.getMessage(), e);
            return null;
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return serviceList;
    }

    public String getHotelPhoneNumberById(long id) {
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            String sql = sqlLoader.getSql("GET_HOTEL_PHONE");

            ps = preparedStatement(sql);
            ps.setLong(1, id);

            // Execute the query
            rs = ps.executeQuery();

            // Check if the result set has any rows
            if (rs.next()) {
                // Retrieve the phone number from the result set
                return rs.getString("hotel_phone_number");
            }
        } catch (SQLException e) {
            log.info(e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        // Return null if no phone number is found for the given ID
        return null;
    }

    public List<String> getHotelsImage(long id){
        ResultSet rs = null;
        PreparedStatement ps = null;
        List<String> roomImages = new ArrayList<>();

        try {
            // Retrieve the SQL query from the XML configuration
            String sql = sqlLoader.getSql("GET_HOTEL_IMAGE");

            // Create the prepared statement with parameters
            ps = preparedStatement(sql);
            ps.setLong(1, id);

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


    public Long getHotelByManagerId(long managerId){
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            String sql = sqlLoader.getSql("GET_HOTEL_ID_BY_BO_ID");

            ps = preparedStatement(sql);
            ps.setLong(1, managerId);

            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getLong("HOTEL_ID");
            }
        } catch (SQLException e) {
            log.error("Error: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return null;
    }

    public HotelsEntity getHotelByRoomId(long roomId) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        HotelsEntity hotel = null;

        try {
            // Retrieve the SQL query from the XML configuration
            String sql = sqlLoader.getSql("GET_HOTEL_BY_ROOM_ID");

            // Create the prepared statement with parameters
            ps = preparedStatement(sql);
            ps.setLong(1, roomId);

            // Execute the query and retrieve the result set
            rs = ps.executeQuery();

            // Process the result set
            if (rs.next()) {
                hotel = new HotelsEntity()
                        .setId(rs.getLong("id"))
                        .setName(rs.getString("hotel_name"))
                        .setStar(rs.getBigDecimal("hotel_star"))
                        .setLattitude(rs.getBigDecimal("lattitude"))
                        .setLongtitude(rs.getBigDecimal("longtitude"))
                        .setDescription(rs.getString("description"))
                        .setNote(rs.getString("note"))
                        .setAddressId(rs.getLong("address_id"))
                        .setBankId(rs.getLong("bank_id"))
                        .setTaxCode(rs.getString("tax_code"));
            }
        } catch (SQLException e) {
            log.error("Error retrieving hotel by room ID: " + e.getMessage(), e);

        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return hotel;
    }

    public String getProvinceNameSuggest(String params) throws Exception {
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            String sql = sqlLoader.getSql("GET_PROVINCE_NAME_SUGGEST");

            ps = preparedStatement(sql);

            ps.setString(1, "%" + params + "%");
            ps.setString(2, "%" + params + "%");
            rs = ps.executeQuery();

            // Process the result set
            if (rs.next()) {
                return rs.getString("PROVINCE_NAME");
            }
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return null;
    }

    public String getDistrictNameSuggest(String params) throws Exception {
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            String sql = sqlLoader.getSql("GET_DISTRICTS_NAME_SUGGEST");

            ps = preparedStatement(sql);

            ps.setString(1, "%" + params + "%");
            ps.setString(2, "%" + params + "%");
            rs = ps.executeQuery();

            // Process the result set
            if (rs.next()) {
                return rs.getString("DISTRICT_NAME");
            }
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return null;
    }

    public List<SuggestHotelResponse> searchHotelsSuggest(String province) throws Exception {
        ResultSet rs = null;
        PreparedStatement ps = null;
        List<SuggestHotelResponse> listHotel = new ArrayList<>();

        try {
            String sql = sqlLoader.getSql("SEARCH_SUGGEST_HOTEL");
            if (StringUtils.isNotEmpty(province)) {
               sql = sql.replace("#CONDITION#", sqlLoader.getSql("CONDITION_PROVINCE"));
            } else {
               sql = sql.replace("#CONDITION#", "");
            }

            ps = preparedStatement(sql);

            if (StringUtils.isNotEmpty(province)) {
                ps.setString(1, province);
            }

            rs = ps.executeQuery();

            // Process the result set
            while (rs.next()) {
                SuggestHotelResponse hotel = new SuggestHotelResponse();
                hotel.setHotelId(rs.getLong("ID"));
                hotel.setHotelName(rs.getString("HOTEL_NAME"));
                hotel.setProvince(rs.getString("PROVINCE_NAME"));
                hotel.setNumberBooked(rs.getLong("ROOM_COUNT"));
                hotel.setImageUrl(rs.getString("IMAGE_URL"));
                listHotel.add(hotel);
            }
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return listHotel;
    }

    public List<SuggestLocationResponse> searchLocationSuggest() throws Exception {
        ResultSet rs = null;
        PreparedStatement ps = null;
        List<SuggestLocationResponse> listLocation = new ArrayList<>();

        try {
            String sql = sqlLoader.getSql("SEARCH_SUGGEST_LOCATION");

            ps = preparedStatement(sql);

            rs = ps.executeQuery();

            // Process the result set
            while (rs.next()) {
                SuggestLocationResponse location = new SuggestLocationResponse();
                location.setProvinceId(rs.getLong("ID"));
                location.setProvinceName(rs.getString("PROVINCE_NAME"));
                location.setNumberBooked(rs.getLong("ROOM_COUNT"));
                location.setImageUrl(rs.getString("IMAGE"));
                listLocation.add(location);
            }
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return listLocation;
    }

    public List<Long> getServiceByHotelId(Long hotelId) throws Exception {
        ResultSet rs = null;
        PreparedStatement ps = null;
        List<Long> listId = new ArrayList<>();

        try {
            String sql = sqlLoader.getSql("GET_SERVICE_HOTEL_BY_ID");



            ps = preparedStatement(sql);
            ps.setLong(1,hotelId);
            rs = ps.executeQuery();

            // Process the result set
            while (rs.next()) {
                listId.add(rs.getLong("HOTEL_SERVICE_ID"));
            }
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return listId;
    }

    public void updateHotel(UpdateHotelRequest hotelsEntity) throws SQLException {
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("UPDATE_HOTEL");
        List<Object> params = new ArrayList<>();
        params.add(hotelsEntity.getName());
        params.add(hotelsEntity.getStar());
        params.add(hotelsEntity.getDescription());
        params.add(hotelsEntity.getNote());
        params.add(hotelsEntity.getTaxCode());
        params.add(hotelsEntity.isIncludesBreakfast());
        params.add(hotelsEntity.isConfirmNow());
        params.add(hotelsEntity.getPhoneNumber());
        params.add(hotelsEntity.getId());
        try {
            ps = preparedStatement(sql, params);
            ps.executeUpdate();
        } finally {
            closePS(ps);
        }
    }

    public void deleteHotelService(Long hotelId) throws Exception {
        PreparedStatement ps = null;

        String sql = sqlLoader.getSql("DELETE_HOTEL_SERVICE");

        try {
            ps = preparedStatement(sql);
            int idx = 0;
            ps.setLong(++idx, hotelId);
            ps.executeUpdate();
        } finally {
            closePS(ps);
        }
    }
    public void updateLastUpdateByHotelId(Long hotelId) throws Exception {
        PreparedStatement ps = null;

        try {
            String sql = sqlLoader.getSql("UPDATE_LAST_TIME_BOOKING");

            ps = preparedStatement(sql);
            ps.setLong(1, hotelId);

            // Execute the update query
            ps.executeUpdate();
        } finally {
            closePS(ps);
        }
    }

    public int totalHotelSystem() throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int totalRecords = 0;
        try {
            String sql = sqlLoader.getSql("COUNT_TOTAL_HOTEL_SYSTEM");
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

    public int totalHotelBusinessAdmin(Long businessAdminId) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int totalRecords = 0;
        try {
            String sql = sqlLoader.getSql("COUNT_TOTAL_HOTEL_BUSINESS_ADMIN");

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

    public void deleteHotelImage(Long hotelId) throws Exception {
        PreparedStatement ps = null;

        String sql = sqlLoader.getSql("DELETE_HOTEL_IMAGE");

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
        String sql = sqlLoader.getSql("DELETE_IMAGE_HOTEL_UPDATE");
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
        String sql = sqlLoader.getSql("GET_LIST_IMAGE_HOTEL_DELETE");
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

    public boolean checkTaxCodeUpdate(String taxCode, Long hotelId) throws Exception {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("CHECK_TAX_CODE_UPDATE");
        try {
            ps = preparedStatement(sql);
            ps.setString(1, taxCode);
            ps.setLong(2, hotelId);
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

    public boolean checkPhoneNumberUpdate(String phoneNumber, Long hotelId) throws Exception {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("CHECK_PHONE_NUMBER_UPDATE");

        try {
            ps = preparedStatement(sql);
            ps.setString(1, phoneNumber);
            ps.setLong(2, hotelId);
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
}
