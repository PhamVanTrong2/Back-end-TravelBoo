package com.bootravel.repository;

import com.bootravel.common.CommonRepository;
import com.bootravel.common.constant.MasterDataConstants;
import com.bootravel.common.dto.PageMetaDTO;
import com.bootravel.entity.*;
import com.bootravel.payload.requests.SearchUserRequest;
import com.bootravel.payload.responses.GetUserByIdResponse;
import com.bootravel.utils.HandlerUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Repository
@Slf4j
public class CustomCommonRepository extends CommonRepository {

    public CustomCommonRepository() throws ParserConfigurationException, IOException, SAXException {
        super();
    }

    @Override
    protected String getFileKey() {
        return "/sql/sqlCommon.xml";
    }

    private List<String> listFieldSearch = Arrays.asList("USER_NAME", "PHONE_NUMBER", "TRIM(CONCAT(FIRST_NAME, ' ', LAST_NAME))");

    public long getSeqAddressId() throws Exception {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("SEQ_ADDRESS");
        try {
            ps = preparedStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong("ADDRESS_ID");
            }
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
        return 0;
    }

    public long getSeqUserId() throws Exception {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("SEQ_USER_ID");
        try {
            ps = preparedStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong("USER_ID");
            }
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
        return 0;
    }

    public long getSeqBookingRoomId() throws Exception {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("SEQ_BOOKING_ROOM_ID");
        try {
            ps = preparedStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong("BOOKING_ROOM_ID");
            }
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
        return 0;
    }

    public long getSeqBookingRoomDetailsId() throws Exception {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("SEQ_BOOKING_ROOM_DETAIL_ID");
        try {
            ps = preparedStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong("BOOKING_ROOM_DETAIL_ID");
            }
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
        return 0;
    }

    public long getSeqTransactionsId() throws Exception {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("SEQ_TRANSACTIONS_ID");
        try {
            ps = preparedStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong("TRANSACTIONS_ID");
            }
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
        return 0;
    }

    public long getSeqPromotionsRedemptionId() throws Exception {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("SEQ_PROMOTION_REDEMPTION_ID");
        try {
            ps = preparedStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong("PROMOTION_REDEMPTION_ID");
            }
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
        return 0;
    }

    public long insertAddress(String address1, String address2, long wardId) throws Exception {
        PreparedStatement ps = null;
        long addressId = getSeqAddressId();
        String sql = sqlLoader.getSql("INSERT_ADDRESS");
        List<Object> params = new ArrayList<>();
        params.add(addressId);
        params.add(address1);
        params.add(address2);
        params.add(wardId);
        try {
            ps = preparedStatement(sql, params);
            ps.executeUpdate();
        } finally {
            closePS(ps);
        }
        return addressId;
    }

    public void insertUser(UsersEntity user) throws Exception {
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("INS_USER");
        List<Object> params = new ArrayList<>();
        params.add(user.getId());
        params.add(user.getUsername());
        params.add(user.getEmail());
        params.add(user.getPassword());
        params.add(user.getFirstName());
        params.add(user.getLastName());
        params.add(user.getPhoneNumber());
        params.add(user.getBirthDate());
        params.add(user.getGender());
        params.add(user.getAddressId());
        params.add(user.getAvatar());
        params.add(user.getRoleId());
        params.add(user.getIdentification());
        try {
            ps = preparedStatement(sql, params);
            ps.executeUpdate();
        } finally {
            closePS(ps);
        }
    }

    public void updateStatus(Long id, UsersEntity user) throws SQLException {
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("UPDATE_STATUS_USERS");
        List<Object> params = new ArrayList<>();
        params.add(user.getStatus());
        params.add(id);
        try {
            ps = preparedStatement(sql, params);
            ps.executeUpdate();
        } finally {
            closePS(ps);
        }
    }

    public RolesEntity getRoleById(Long id) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = sqlLoader.getSql("SELECT_ROLE_BY_ID");
        List<Object> params = new ArrayList<>();
        params.add(id);
        try {
            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();
            if (rs.next()) {
                RolesEntity user = new RolesEntity();
                user.setId(rs.getLong("ID"));
                user.setName(rs.getString("NAME"));
                return user;
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

    public GetUserByIdResponse getUserById(Long id) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = sqlLoader.getSql("GET_USER_BY_ID");
        List<Object> params = new ArrayList<>();
        params.add(id);
        try {
            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();
            if (rs.next()) {
                GetUserByIdResponse user = new GetUserByIdResponse();
                user.setId(rs.getLong("ID"));
                user.setUserName(rs.getString("USER_NAME"));
                user.setEmail(rs.getString("EMAIL"));
                user.setFirstName(rs.getString("FIRST_NAME"));
                user.setLastName(rs.getString("LAST_NAME"));
                user.setPhoneNumber(rs.getString("PHONE_NUMBER"));
                user.setBirthDate(rs.getDate("BIRTH_DATE"));
                user.setGender(rs.getString("GENDER"));
                user.setAvatar(rs.getString("AVATAR"));
                user.setStatus(rs.getBoolean("STATUS"));
                user.setRoleId(rs.getLong("ROLE_ID"));
                user.setAddress1(rs.getString("ADDRESS1"));
                user.setAddress2(rs.getString("ADDRESS2"));
                user.setWardId(rs.getLong("WARD_ID"));
                user.setDistrictId(rs.getLong("DISTRICT_ID"));
                user.setProvinceId(rs.getLong("PROVINCE_ID"));
                user.setWardName(rs.getString("WARD_NAME"));
                user.setDistrictName(rs.getString("DISTRICT_NAME"));
                user.setProvinceName(rs.getString("PROVINCE_NAME"));
                user.setIdentification(rs.getString("IDENTIFICATION"));
                return user;
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

    public Integer getTotalUserOutput(SearchUserRequest request, Long roleId) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int totalRecords = 0;
        try {
            //ＳＱＬ文の取得
            String sql = sqlLoader.getSql("COUNT_USER_OUTPUT");
            sql = sql.replace(MasterDataConstants.EXTENDS_CONDITION,
                    HandlerUtils.createFullTextSearchQuery(listFieldSearch, request.getSearchParams(), true));
            if (Objects.nonNull(roleId)) {
                sql = sql.replace("#EXTENDS_ROLD#", "AND ROLE_ID = " + roleId);
            } else {
                sql = sql.replace("#EXTENDS_ROLD#", "");
            }
            // excute query
            ps = preparedStatement(sql);

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

    public StringBuilder appendSqlSearchUserCondition(SearchUserRequest searchConditon) {
        StringBuilder sqlBuilder = new StringBuilder();
        // with 部署選択 condition
        if (StringUtils.isNotEmpty(searchConditon.getSearchParams())) {
            String[] condition = searchConditon.getSearchParams().split(MasterDataConstants.COMMA);
            sqlBuilder.append(MasterDataConstants.SQL_AND).append(HandlerUtils.appendInCondition("USER_NAME", condition))
                    .append(MasterDataConstants.SQL_OR)
                    .append(HandlerUtils.appendInCondition("PHONE_NUMBER", condition))
                    .append(MasterDataConstants.SQL_OR)
                    .append(HandlerUtils.appendInCondition("TRIM(CONCAT(FIRST_NAME, ' ', LAST_NAME))", condition))
            ;
        }
        return sqlBuilder;
    }

    public List<UsersEntity> searchUser(SearchUserRequest searchUserRequest, long role) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        List<UsersEntity> listUsers = new ArrayList<>();
        String sortBy = setSortBySqlColumnName(searchUserRequest.getSearchPaging().getSortBy());
        searchUserRequest.getSearchPaging().setSortBy(sortBy);

        String sql = sqlLoader.getSql("SEARCH_USER");
        sql = sql.replace(MasterDataConstants.EXTENDS_CONDITION,
                HandlerUtils.createFullTextSearchQuery(listFieldSearch, searchUserRequest.getSearchParams(), true));
        StringBuilder pagingQuery = HandlerUtils.appendSortQuery(searchUserRequest.getSearchPaging());
        sql = sql.replace(MasterDataConstants.EXTENDS_PAGING, pagingQuery.toString());

        try {
            ps = preparedStatement(sql);
            int idx = 0;
            ps.setLong(++idx, role);
            rs = ps.executeQuery();
            while (rs.next()) {
                UsersEntity user = new UsersEntity();
                user.setId(rs.getLong("ID"));
                user.setUsername(rs.getString("USER_NAME"));
                user.setEmail(rs.getString("EMAIL"));
                user.setFirstName(rs.getString("FIRST_NAME"));
                user.setLastName(rs.getString("LAST_NAME"));
                user.setPhoneNumber(rs.getString("PHONE_NUMBER"));
                user.setBirthDate(rs.getDate("BIRTH_DATE"));
                user.setGender(rs.getString("GENDER"));
                user.setAddressId(rs.getLong("ADDRESS_ID"));
                user.setAvatar(rs.getString("AVATAR"));
                user.setStatus(rs.getBoolean("STATUS"));
                user.setRoleId(rs.getLong("ROLE_ID"));
                listUsers.add(user);
            }
        } catch (SQLException e) {
            log.info(e.getMessage(), e);
            return null;
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
        return listUsers;

    }


    public String setSortBySqlColumnName(String orderBy) {
        switch (orderBy) {
            case "userName":
                return "USER_NAME";
            case "id":
                return "ID";
            case "email":
                return "EMAIL";
            case "fullName":
                return "FULL_NAME";
            case "status":
                return "STATUS";
            default:
                return "";
        }
    }

    public boolean isExistUser(Long id) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("CHECK_EXIST_BY_ID");
        List<Object> params = new ArrayList<>();
        params.add(id);
        try {
            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            log.info(e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
        return false;
    }

    public void insertEmployeeOf(Long userId, Long managerId) throws SQLException {
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("INSERT_EMPLOYEE_OF");
        List<Object> params = new ArrayList<>();
        params.add(userId);
        params.add(managerId);
        try {
            ps = preparedStatement(sql, params);
            ps.executeUpdate();
        } finally {
            closePS(ps);
        }
    }

    public void insertStaffOf(Long userId, Long managerId, Long hotelId) throws SQLException {
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("INSERT_STAFF_OF");
        List<Object> params = new ArrayList<>();
        params.add(userId);
        params.add(managerId);
        params.add(hotelId);
        try {
            ps = preparedStatement(sql, params);
            ps.executeUpdate();
        } finally {
            closePS(ps);
        }
    }

    public long getSeqHotelId() throws Exception {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("SEQ_HOTEL_ID");
        try {
            ps = preparedStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong("HOTEL_ID");
            }
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
        return 0;
    }

    public long getSeqHotelImage() throws Exception {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("SEQ_HOTEL_IMAGE");
        try {
            ps = preparedStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong("HOTEL_IMAGE_ID");
            }
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
        return 0;
    }

    public List<BedType> getBedType() throws Exception {
        ResultSet rs = null;
        PreparedStatement ps = null;
        List<BedType> bedTypeList = new ArrayList<>();
        String sql = sqlLoader.getSql("SELECT_BED_TYPE");
        try {
            ps = preparedStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                BedType bedType = new BedType();
                bedType.setId(rs.getLong("ID"));
                bedType.setName(rs.getString(("BED_TYPE_NAME")));

                bedTypeList.add(bedType);
            }
        } catch (SQLException e) {
            log.info(e.getMessage(), e);
            return null;
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
        return bedTypeList;
    }

    public List<RoomTypesEntity> getAllRoomType() throws Exception {
        ResultSet rs = null;
        PreparedStatement ps = null;
        List<RoomTypesEntity> roomTypeList = new ArrayList<>();
        String sql = sqlLoader.getSql("SELECT_ROOM_TYPE");
        try {
            ps = preparedStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                RoomTypesEntity roomTypesEntity = new RoomTypesEntity();
                roomTypesEntity.setId(rs.getLong("ID"));
                roomTypesEntity.setName(rs.getString(("ROOM_TYPE_NAME")));

                roomTypeList.add(roomTypesEntity);
            }
        } catch (SQLException e) {
            log.info(e.getMessage(), e);
            return null;
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
        return roomTypeList;
    }
    
    public GetUserByIdResponse getEmployeeById(Long id, Long managerId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = sqlLoader.getSql("GET_EMPLOYEE_BY_ID");
        List<Object> params = new ArrayList<>();
        params.add(id);
        params.add(managerId);
        try {
            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();
            if (rs.next()) {
                GetUserByIdResponse user = new GetUserByIdResponse();
                user.setId(rs.getLong("ID"));
                user.setUserName(rs.getString("USER_NAME"));
                user.setEmail(rs.getString("EMAIL"));
                user.setFirstName(rs.getString("FIRST_NAME"));
                user.setLastName(rs.getString("LAST_NAME"));
                user.setPhoneNumber(rs.getString("PHONE_NUMBER"));
                user.setBirthDate(rs.getDate("BIRTH_DATE"));
                user.setGender(rs.getString("GENDER"));
                user.setAvatar(rs.getString("AVATAR"));
                user.setStatus(rs.getBoolean("STATUS"));
                user.setRoleId(rs.getLong("ROLE_ID"));
                user.setAddress1(rs.getString("ADDRESS1"));
                user.setAddress2(rs.getString("ADDRESS2"));
                user.setWardId(rs.getLong("WARD_ID"));
                user.setDistrictId(rs.getLong("DISTRICT_ID"));
                user.setProvinceId(rs.getLong("PROVINCE_ID"));
                user.setWardName(rs.getString("WARD_NAME"));
                user.setDistrictName(rs.getString("DISTRICT_NAME"));
                user.setProvinceName(rs.getString("PROVINCE_NAME"));
                user.setIdentification(rs.getString("IDENTIFICATION"));
                return user;
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
    public List<UsersEntity> searchEmployee(SearchUserRequest searchUserRequest, long role, long managerId) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        List<UsersEntity> listUsers = new ArrayList<>();
        String sortBy = setSortBySqlColumnName(searchUserRequest.getSearchPaging().getSortBy());
        searchUserRequest.getSearchPaging().setSortBy(sortBy);
        String sql = sqlLoader.getSql("SEARCH_EMPLOYEE");
        sql = sql.replace(MasterDataConstants.EXTENDS_CONDITION,
                HandlerUtils.createFullTextSearchQuery(listFieldSearch, searchUserRequest.getSearchParams(), true));
        StringBuilder pagingQuery = HandlerUtils.appendSortQuery(searchUserRequest.getSearchPaging());
        sql = sql.replace(MasterDataConstants.EXTENDS_PAGING, pagingQuery.toString());
        try {
            ps = preparedStatement(sql);
            int idx = 0;
            ps.setLong(++idx, role);
            ps.setLong(++idx, managerId);
            rs = ps.executeQuery();
            while (rs.next()) {
                UsersEntity user = new UsersEntity();
                user.setId(rs.getLong("ID"));
                user.setUsername(rs.getString("USER_NAME"));
                user.setEmail(rs.getString("EMAIL"));
                user.setFirstName(rs.getString("FIRST_NAME"));
                user.setLastName(rs.getString("LAST_NAME"));
                user.setPhoneNumber(rs.getString("PHONE_NUMBER"));
                user.setBirthDate(rs.getDate("BIRTH_DATE"));
                user.setGender(rs.getString("GENDER"));
                user.setAddressId(rs.getLong("ADDRESS_ID"));
                user.setAvatar(rs.getString("AVATAR"));
                user.setStatus(rs.getBoolean("STATUS"));
                user.setRoleId(rs.getLong("ROLE_ID"));
                listUsers.add(user);
            }
        } catch (SQLException e) {
            log.info(e.getMessage(), e);
            return null;
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
        return listUsers;
    }

    public Integer getTotalEmployeeOutput(SearchUserRequest request, Long roleId, Long managerId) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int totalRecords = 0;
        try {
            //ＳＱＬ文の取得
            String sql = sqlLoader.getSql("COUNT_EMPLOYEE_OUTPUT");
            sql = sql.replace(MasterDataConstants.EXTENDS_CONDITION,
                    HandlerUtils.createFullTextSearchQuery(listFieldSearch, request.getSearchParams(), true));
            if (Objects.nonNull(roleId)) {
                sql = sql.replace("#EXTENDS_ROLE#", "AND ROLE_ID = " + roleId);
            } else {
                sql = sql.replace("#EXTENDS_ROLE#", "");
            }

            List<Object> params = new ArrayList<>();
            params.add(managerId);

            ps = preparedStatement(sql, params);
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

    public Integer getTotalStaffOutput(SearchUserRequest request, Long managerId) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int totalRecords = 0;
        try {
            //ＳＱＬ文の取得
            String sql = sqlLoader.getSql("COUNT_EMPLOYEE_OUTPUT");
            sql = sql.replace(MasterDataConstants.EXTENDS_CONDITION,
                    HandlerUtils.createFullTextSearchQuery(listFieldSearch, request.getSearchParams(), true));

            String txt = "AND ( ROLE_ID = " + MasterDataConstants.ROLE_BOOKING_STAFF + " OR ROLE_ID = " + MasterDataConstants.ROLE_TRANSACTION_STAFF + " )" ;
            sql = sql.replace("#EXTENDS_ROLE#", txt);
            List<Object> params = new ArrayList<>();
            params.add(managerId);

            ps = preparedStatement(sql, params);
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

    public List<UsersEntity> searchStaff(SearchUserRequest searchUserRequest, long managerId) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        List<UsersEntity> listUsers = new ArrayList<>();
        String sortBy = setSortBySqlColumnName(searchUserRequest.getSearchPaging().getSortBy());
        searchUserRequest.getSearchPaging().setSortBy(sortBy);
        String sql = sqlLoader.getSql("SEARCH_STAFF");
        sql = sql.replace(MasterDataConstants.EXTENDS_CONDITION,
                HandlerUtils.createFullTextSearchQuery(listFieldSearch, searchUserRequest.getSearchParams(), true));
        StringBuilder pagingQuery = HandlerUtils.appendSortQuery(searchUserRequest.getSearchPaging());
        sql = sql.replace(MasterDataConstants.EXTENDS_PAGING, pagingQuery.toString());
        try {
            ps = preparedStatement(sql);
            int idx = 0;
            ps.setLong(++idx, managerId);
            rs = ps.executeQuery();
            while (rs.next()) {
                UsersEntity user = new UsersEntity();
                user.setId(rs.getLong("ID"));
                user.setUsername(rs.getString("USER_NAME"));
                user.setEmail(rs.getString("EMAIL"));
                user.setFirstName(rs.getString("FIRST_NAME"));
                user.setLastName(rs.getString("LAST_NAME"));
                user.setPhoneNumber(rs.getString("PHONE_NUMBER"));
                user.setBirthDate(rs.getDate("BIRTH_DATE"));
                user.setGender(rs.getString("GENDER"));
                user.setAddressId(rs.getLong("ADDRESS_ID"));
                user.setAvatar(rs.getString("AVATAR"));
                user.setStatus(rs.getBoolean("STATUS"));
                user.setRoleId(rs.getLong("ROLE_ID"));
                listUsers.add(user);
            }
        } catch (SQLException e) {
            log.info(e.getMessage(), e);
            return null;
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
        return listUsers;
    }

    public void updateAddress(String address1, String address2, Long wardId, Long hotelId) throws Exception {
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("UPDATE_ADDRESS");
        List<Object> params = new ArrayList<>();

        params.add(address1);
        params.add(address2);
        params.add(wardId);
        params.add(hotelId);
        try {
            ps = preparedStatement(sql, params);
            ps.executeUpdate();
        } finally {
            closePS(ps);
        }
    }


    public void updateHotelIdNull(Long boId) throws Exception {
        PreparedStatement ps = null;

        String sql = sqlLoader.getSql("UPDATE_HOTEL_IS_NULL");

        try {
            ps = preparedStatement(sql);
            int idx = 0;
            ps.setLong(++idx, boId);
            ps.executeUpdate();
        } finally {
            closePS(ps);
        }
    }

    public void updateManager(Long oldManager, Long newManager) throws SQLException {
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("UPDATE_MANAGER");
        List<Object> params = new ArrayList<>();
        params.add(newManager);
        params.add(oldManager);
        try {
            ps = preparedStatement(sql, params);
            ps.executeUpdate();
        } finally {
            closePS(ps);
        }
    }
}
