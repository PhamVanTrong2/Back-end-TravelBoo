package com.bootravel.repository;

import com.bootravel.common.CommonRepository;
import com.bootravel.common.constant.MasterDataConstants;
import com.bootravel.common.constant.StatusPromotionConstants;
import com.bootravel.entity.PromotionRedemptionsEntity;
import com.bootravel.entity.PromotionsEntity;
import com.bootravel.entity.UsersEntity;
import com.bootravel.payload.requests.SearchPromotionRequest;
import com.bootravel.payload.requests.UpdatePromotionRequest;
import com.bootravel.payload.requests.UpdateStatusPromotionRequest;
import com.bootravel.payload.responses.SearchPromotionResponse;
import com.bootravel.utils.HandlerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Repository
@Slf4j
public class PromotionRepository extends CommonRepository {

    private List<String> listFieldSearch = Arrays.asList("PROMOTION_CODE", "PROMOTION_NAME");

    public PromotionRepository() throws ParserConfigurationException, IOException, SAXException {
        super();
    }

    @Override
    protected String getFileKey() {
        return "/sql/sqlPromotionRepository.xml";
    }

    public Integer getTotalPromotionOutput(SearchPromotionRequest request) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int totalRecords = 0;
        try {
            String sql = sqlLoader.getSql("COUNT_PROMOTIONS_OUTPUT");
            //sql = sql.replace(MasterDataConstants.EXTENDS_CONDITION, appendSqlSearchPromotionCondition(request).toString());
            sql = sql.replace(MasterDataConstants.EXTENDS_CONDITION,
                    HandlerUtils.createFullTextSearchQuery(listFieldSearch, request.getSearchParams(), true));
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

    public StringBuilder appendSqlSearchPromotionCondition(SearchPromotionRequest searchCondition) {
        StringBuilder sqlBuilder = new StringBuilder();
        if (searchCondition.getSearchParams() != null) {
            String[] condition = searchCondition.getSearchParams().split(MasterDataConstants.COMMA);
            sqlBuilder.append(MasterDataConstants.SQL_AND)
                    .append(HandlerUtils.appendInCondition("PROMOTION_CODE", condition))
                    .append(MasterDataConstants.SQL_OR)
                    .append(HandlerUtils.appendInCondition("PROMOTION_NAME", condition))
            ;
        }
        return sqlBuilder;
    }

    public List<SearchPromotionResponse> searchPromotion(SearchPromotionRequest request) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        List<SearchPromotionResponse> listPromotion = new ArrayList<>();
        String sortBy = setSortBySqlColumnName(request.getSearchPaging().getSortBy());
        request.getSearchPaging().setSortBy(sortBy);

        String sql = sqlLoader.getSql("SEARCH_PROMOTION");

        //sql = sql.replace(MasterDataConstants.EXTENDS_CONDITION, appendSqlSearchPromotionCondition(request).toString());
        sql = sql.replace(MasterDataConstants.EXTENDS_CONDITION,
                HandlerUtils.createFullTextSearchQuery(listFieldSearch, request.getSearchParams(), true));
        StringBuilder pagingQuery = HandlerUtils.appendSortQuery(request.getSearchPaging());
        sql = sql.replace(MasterDataConstants.EXTENDS_PAGING, pagingQuery.toString());

        try {
            ps = preparedStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                SearchPromotionResponse response = new SearchPromotionResponse();

                response.setId(rs.getLong("ID"));
                response.setCode(rs.getString("PROMOTION_CODE"));
                response.setName(rs.getString("PROMOTION_NAME"));
                response.setStartDate(rs.getDate("START_DATE"));
                response.setEndDate(rs.getDate("END_DATE"));
                if(rs.getString("STATUS").equals(String.valueOf(StatusPromotionConstants.ACTIVE))) {
                    response.setStatus("Đang hoạt động");
                } else if(rs.getString("STATUS").equals(String.valueOf(StatusPromotionConstants.PAUSE))) {
                    response.setStatus("Tạm dừng");
                } else {
                    response.setStatus("Hết hiệu lực");
                }
                response.setImageUrl(rs.getString("IMAGE_URL"));
                listPromotion.add(response);
            }
        } catch (SQLException e) {
            log.info(e.getMessage(), e);
            return null;
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
        return listPromotion;
    }

    public String setSortBySqlColumnName(String orderBy) {
        switch (orderBy) {
            case "createdDate":
                return "CREATED_DATE";
            case "id":
                return "ID";
            default:
                return "";
        }
    }

    public boolean isExistCodePromotion(String code) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("CHECK_EXIST_CODE_PROMOTION");
        try {
            List<Object> params = new ArrayList<>();
            params.add(code);
            ps = preparedStatement(sql, params);

            rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }

        } catch (SQLException e) {
            log.error("Error check exist code promotion: " + e.getMessage(), e);
        } finally {
            closePS(ps);
            closeResultSet(rs);
        }
        return false;
    }

    public boolean isExistPromotion(Long id) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("CHECK_EXIST_PROMOTION");
        try {
            List<Object> params = new ArrayList<>();
            params.add(id);
            ps = preparedStatement(sql, params);

            rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }

        } catch (SQLException e) {
            log.error("Error check exist promotion: " + e.getMessage(), e);
        } finally {
            closePS(ps);
            closeResultSet(rs);
        }
        return false;
    }

    public void createPromotion(PromotionsEntity promotion) {
        PreparedStatement ps = null;

        String sql = sqlLoader.getSql("INSERT_PROMOTION");

        try {
            List<Object> params = new ArrayList<>();
            params.add(promotion.getCode());
            params.add(promotion.getName());
            params.add(promotion.getImageUrl());
            params.add(promotion.getDescription());
            params.add(promotion.getStartDate());
            params.add(promotion.getEndDate());
            params.add(promotion.getTypePromotion());
            params.add(promotion.getDiscountPercent());
            params.add(promotion.getMaxDiscount());
            params.add(promotion.getFixMoneyDiscount());
            params.add(promotion.getTypeMaxUse());
            params.add(promotion.getMaxUse());
            params.add(promotion.getStatus());
            params.add(promotion.getCreatedDate());
            params.add(promotion.getModifiedDate());

            ps = preparedStatement(sql, params);

            ps.executeUpdate();

        } catch (SQLException e) {
            log.error("Error inserting promotion: " + e.getMessage(), e);
        } finally {
            closePS(ps);
        }
    }

    public void updateStatus(UpdateStatusPromotionRequest request, Timestamp lastModifiedTimestamp) {
        PreparedStatement ps = null;

        String sql = sqlLoader.getSql("UPDATE_STATUS_PROMOTION");

        try {
            List<Object> params = new ArrayList<>();
            if(request.getStatus().equals("Đang hoạt động")) {
                params.add(String.valueOf(StatusPromotionConstants.ACTIVE));
            } else {
                params.add(String.valueOf(StatusPromotionConstants.PAUSE));
            }
            params.add(lastModifiedTimestamp);
            params.add(request.getId());
            ps = preparedStatement(sql, params);

            ps.executeUpdate();

        } catch (SQLException e) {
            log.error("Error update status promotion: " + e.getMessage(), e);
        } finally {
            closePS(ps);
        }
    }
    public PromotionsEntity getPromotionById(Long id) {
        ResultSet rs = null;
        PreparedStatement ps = null;

        String sql = sqlLoader.getSql("SELECT_PROMOTION_BY_ID");

        try {
            List<Object> params = new ArrayList<>();
            params.add(id);

            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();

            if (rs.next()) {
                PromotionsEntity entity = new PromotionsEntity();
                entity.setId(rs.getLong("ID"));
                entity.setCode(rs.getString("PROMOTION_CODE"));
                entity.setName(rs.getString("PROMOTION_NAME"));
                entity.setImageUrl(rs.getString("IMAGE_URL"));
                entity.setDescription(rs.getString("DESCRIPTION"));
                entity.setStartDate(rs.getDate("START_DATE"));
                entity.setEndDate(rs.getDate("END_DATE"));
                entity.setTypePromotion(rs.getInt("TYPE_PROMOTION"));
                entity.setDiscountPercent(rs.getInt("DISCOUNT_PERCENT"));
                entity.setMaxDiscount(rs.getBigDecimal("MAX_DISCOUNT"));
                entity.setFixMoneyDiscount(rs.getBigDecimal("FIX_MONEY_DISCOUNT"));
                entity.setTypeMaxUse(rs.getInt("TYPE_MAX_USE"));
                entity.setMaxUse(rs.getInt("MAX_USE"));
                entity.setCreatedDate(rs.getTimestamp("CREATED_DATE"));
                entity.setModifiedDate(rs.getTimestamp("MODIFIED_DATE"));
                entity.setStatus(rs.getString("STATUS"));

                return entity;
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

    public void update(UpdatePromotionRequest request, Timestamp lastModifiedTimestamp) {
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("UPDATE_PROMOTION");
        try {
            List<Object> params = new ArrayList<>();
            params.add(request.getName());
            params.add(request.getDescription());
            params.add(request.getStartDate());
            params.add(request.getEndDate());
            params.add(request.getTypePromotion());
            params.add(request.getDiscountPercent());
            params.add(request.getMaxDiscount());
            params.add(request.getFixMoneyDiscount());
            params.add(request.getTypeMaxUse());
            params.add(request.getMaxUse());
            params.add(lastModifiedTimestamp);
            params.add(request.getId());
            ps = preparedStatement(sql, params);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error update status promotion: " + e.getMessage(), e);
        } finally {
            closePS(ps);
        }
    }
    @Scheduled(cron ="0 0 0 * * *", zone = "Asia/Ho_Chi_Minh")
    @Async
    public void updateStatusSchedule()
    {
        Date dateNow = new Date(System.currentTimeMillis());
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("UPDATE_STATUS_PROMOTION_SCHEDULE");
        try {
            List<Object> params = new ArrayList<>();
            params.add(String.valueOf(StatusPromotionConstants.EXPIRED));
            params.add(dateNow);
            ps = preparedStatement(sql, params);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error update status schedule promotion: " + e.getMessage(), e);
        } finally {
            log.info("Updated status schedule every day");
            closePS(ps);
        }
    }

    public PromotionsEntity getPromotionByCode(String code) {
        ResultSet rs = null;
        PreparedStatement ps = null;

        String sql = sqlLoader.getSql("SELECT_PROMOTION_BY_CODE");

        try {
            List<Object> params = new ArrayList<>();
            params.add(code);

            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();

            if (rs.next()) {
                PromotionsEntity entity = new PromotionsEntity();
                entity.setId(rs.getLong("ID"));
                entity.setCode(rs.getString("PROMOTION_CODE"));
                entity.setName(rs.getString("PROMOTION_NAME"));
                entity.setImageUrl(rs.getString("IMAGE_URL"));
                entity.setDescription(rs.getString("DESCRIPTION"));
                entity.setStartDate(rs.getDate("START_DATE"));
                entity.setEndDate(rs.getDate("END_DATE"));
                entity.setTypePromotion(rs.getInt("TYPE_PROMOTION"));
                entity.setDiscountPercent(rs.getInt("DISCOUNT_PERCENT"));
                entity.setMaxDiscount(rs.getBigDecimal("MAX_DISCOUNT"));
                entity.setFixMoneyDiscount(rs.getBigDecimal("FIX_MONEY_DISCOUNT"));
                entity.setTypeMaxUse(rs.getInt("TYPE_MAX_USE"));
                entity.setMaxUse(rs.getInt("MAX_USE"));
                entity.setCreatedDate(rs.getTimestamp("CREATED_DATE"));
                entity.setModifiedDate(rs.getTimestamp("MODIFIED_DATE"));
                entity.setStatus(rs.getString("STATUS"));

                return entity;
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

    public PromotionsEntity updateMaxUsePromotion(long id, int newStatus) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("UPDATE_MAX_USE_PROMOTION");

        try {
            // Create a list to hold the query parameters
            List<Object> params = new ArrayList<>();
            params.add(newStatus); // For the first placeholder (MAX_USE)
            params.add(id);     // For the second placeholder (ID)

            // Create the prepared statement with parameters
            ps = preparedStatement(sql, params);

            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated > 0) {
                // The user status was successfully updated
                PromotionsEntity updated = new PromotionsEntity();
                updated.setId(id); // Set the user ID
                updated.setMaxUse(newStatus); // Set the new status
                return updated;
            }
        } catch (SQLException e) {
            log.error("Error updating user status: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return null; // Return null if the update was not successful
    }

   //--- promotion redemption ---\\

    public PromotionRedemptionsEntity insertPromotionRedemptions(PromotionRedemptionsEntity promotionRedemption) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = sqlLoader.getSql("INSERT_PROMOTION_REDEMPTION");

        try {
            List<Object> params = new ArrayList<>();
            params.add(promotionRedemption.getId());
            params.add(promotionRedemption.getUserId());
            params.add(promotionRedemption.getPromotionId());
            params.add(promotionRedemption.getTransactionId());
            params.add(promotionRedemption.getRedeemedAmount());
            params.add(promotionRedemption.getRedemptionDate());

            ps = preparedStatement(sql, params);

            int rowsInserted = ps.executeUpdate();

            if (rowsInserted > 0) {
                return promotionRedemption;
            }
        } catch (SQLException e) {
            log.error("Error inserting promotion redemption: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return null;  // Return null if the insertion was not successful
    }

    public boolean checkUserUsedPromotion(long userId, long promotionId) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = sqlLoader.getSql("CHECK_USER_USED_PROMOTION");

        try {
            // Create a list to hold the query parameters
            List<Object> params = new ArrayList<>();
            params.add(userId); // For the first placeholder (USER_ID)
            params.add(promotionId); // For the second placeholder (PROMOTION_ID)

            // Create the prepared statement with parameters
            ps = preparedStatement(sql, params);

            // Execute the query and obtain the result set
            rs = ps.executeQuery();

            // Process the result set if needed (check if user used promotion)
            boolean userUsedPromotion = rs.next();

            return userUsedPromotion;
        } catch (SQLException e) {
            log.error("Error checking user promotion usage: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return false; // Return false if there was an error during execution
    }

    public int totalPromotionSystem() {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int totalRecords = 0;
        try {
            String sql = sqlLoader.getSql("COUNT_TOTAL_PROMOTION_SYSTEM");
            ps = preparedStatement(sql);
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

    public List<SearchPromotionResponse> searchPublicPromotion() {
        ResultSet rs = null;
        PreparedStatement ps = null;
        List<SearchPromotionResponse> listPromotion = new ArrayList<>();

        String sql = sqlLoader.getSql("SEARCH_PUBLIC_PROMOTION");
        List<Object> params = new ArrayList<>();
        params.add(String.valueOf(StatusPromotionConstants.ACTIVE));
        try {
            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();
            while (rs.next()) {
                SearchPromotionResponse response = new SearchPromotionResponse();

                response.setId(rs.getLong("ID"));
                response.setCode(rs.getString("PROMOTION_CODE"));
                response.setName(rs.getString("PROMOTION_NAME"));
                response.setStartDate(rs.getDate("START_DATE"));
                response.setEndDate(rs.getDate("END_DATE"));
                response.setStatus("Đang hoạt động");
//                if(rs.getString("STATUS").equals(String.valueOf(StatusPromotionConstants.ACTIVE))) {
//                    response.setStatus("Đang hoạt động");
//                }
                response.setImageUrl(rs.getString("IMAGE_URL"));
                listPromotion.add(response);
            }
        } catch (SQLException e) {
            log.info(e.getMessage(), e);
            return null;
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
        return listPromotion;
    }

}
