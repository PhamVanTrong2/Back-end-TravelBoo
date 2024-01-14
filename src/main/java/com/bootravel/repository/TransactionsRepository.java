package com.bootravel.repository;

import com.bootravel.common.CommonRepository;
import com.bootravel.common.constant.MasterDataConstants;
import com.bootravel.common.constant.TransactionConstants;
import com.bootravel.entity.TransactionsEntity;
import com.bootravel.payload.requests.SearchTransactionRequest;
import com.bootravel.payload.responses.*;
import com.bootravel.utils.HandlerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
@Slf4j
public class TransactionsRepository extends CommonRepository {

    private List<String> listFieldSearchTransactionSystem = Arrays.asList("user_name", "hotel_name", "payment_method_name");
    private List<String> listFieldSearchTransactionBE = Arrays.asList("user_name", "payment_method_name");

    public TransactionsRepository() throws ParserConfigurationException, IOException, SAXException {
        super();
    }
    @Override
    protected String getFileKey() {
        return "/sql/sqlTransactionsRepository.xml";
    }

    public TransactionsEntity insertTransactions(TransactionsEntity request) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = sqlLoader.getSql("INSERT_TRANSACTIONS");

        try {
            List<Object> params = new ArrayList<>();
            params.add(request.getId());
            params.add(request.getUserId());
            params.add(request.getPaymentMethodId());
            params.add(request.getAmount());
            params.add(request.getBookingRoomId());
            params.add(request.getStatus());
            ps = preparedStatement(sql, params);
            ps.executeUpdate();

            return request;

        } catch (SQLException e) {
            log.error("Error inserting booking room detail: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return null;  // Return null if the insertion was not successful
    }

    public TransactionsEntity updateTransactionStatus(Long transactionId, Integer newStatus) {
        PreparedStatement ps = null;

        String sql = sqlLoader.getSql("UPDATE_TRANSACTION_STATUS");

        try {
            List<Object> params = new ArrayList<>();
            params.add(newStatus);
            params.add(transactionId);

            ps = preparedStatement(sql, params);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                // Successfully updated the status
                log.info("Transaction status updated successfully for ID: " + transactionId);
                return getTransactionById(transactionId);
            } else {
                log.warn("No rows were affected. Transaction status update failed for ID: " + transactionId);
            }
        } catch (SQLException e) {
            log.error("Error updating transaction status: " + e.getMessage(), e);
        } finally {
            closePS(ps);
        }

        return null;  // Return null if the update was not successful
    }

    public TransactionsEntity getTransactionById(Long transactionId) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = sqlLoader.getSql("SELECT_TRANSACTION_BY_ID");

        try {
            List<Object> params = new ArrayList<>();
            params.add(transactionId);

            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();

            if (rs.next()) {
                // Mapping result set to TransactionsEntity
                TransactionsEntity transaction = new TransactionsEntity();
                transaction.setId(rs.getLong("id"));
                transaction.setUserId(rs.getInt("user_id"));
                transaction.setPaymentMethodId(rs.getInt("payment_method_id"));
                transaction.setAmount(rs.getBigDecimal("amount"));
                transaction.setTransactionTime(rs.getTimestamp("transaction_time"));
                transaction.setBookingRoomId(rs.getInt("booking_room_id"));
                transaction.setStatus(rs.getInt("status"));

                return transaction;
            } else {
                log.warn("No transaction found with ID: " + transactionId);
            }
        } catch (SQLException e) {
            log.error("Error retrieving transaction by ID: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return null;  // Return null if the transaction is not found or an error occurs
    }


    public Integer getTotalTransactionSystemOutput(SearchTransactionRequest request) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int totalRecords = 0;
        try {
            String sql = sqlLoader.getSql("COUNT_TRANSACTION_SYSTEM_OUTPUT");
            sql = sql.replace(MasterDataConstants.EXTENDS_CONDITION,
                    HandlerUtils.createFullTextSearchQuery(listFieldSearchTransactionSystem, request.getSearchParams(), true));
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

    public List<SearchTransactionSystemResponse> searchTransactionSystem(SearchTransactionRequest request) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<SearchTransactionSystemResponse> listResponse = new ArrayList<>();
        String sql = sqlLoader.getSql("SELECT_TRANSACTION_SYSTEM");
        sql = sql.replace(MasterDataConstants.EXTENDS_CONDITION,
                HandlerUtils.createFullTextSearchQuery(listFieldSearchTransactionSystem, request.getSearchParams(), true));
        StringBuilder pagingQuery = HandlerUtils.appendSortQuery(request.getSearchPaging());
        sql = sql.replace(MasterDataConstants.EXTENDS_PAGING, pagingQuery.toString());
        try {
            List<Object> params = new ArrayList<>();

            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();

            while (rs.next()) {
                SearchTransactionSystemResponse response = new SearchTransactionSystemResponse();
                response.setId(rs.getLong("id"));
                response.setCustomerName(rs.getString("user_name"));
                response.setHotelName(rs.getString("hotel_name"));
                response.setPaymentMethod(rs.getString("payment_method_name"));
                response.setAmount(rs.getBigDecimal("amount"));
                response.setAmountSystemReceiver(response.getAmount().multiply(new BigDecimal(15)).divide(new BigDecimal(100)));
                response.setTransactionTime(rs.getTimestamp("transaction_time"));
                int status = rs.getInt("status");
                if(status == TransactionConstants.SUCCESS) {
                    response.setStatus("Hoàn thành");
                } else if(status == TransactionConstants.NOT_FINISH) {
                    response.setStatus("Chưa hoàn thành");
                }
                else if(status == TransactionConstants.PENDING) {
                    response.setStatus("Đang chờ duyệt");
                }
                else if(status == TransactionConstants.REVERSED_TRANSACTION) {
                    response.setStatus("Hoàn trả");
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

    public GetTransactionByIdResponse getTransactionDetailById(Long id) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = sqlLoader.getSql("SELECT_TRANSACTION_SYSTEM_DETAIL_BY_ID");

        try {
            List<Object> params = new ArrayList<>();
            params.add(id);

            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();

            if (rs.next()) {
                GetTransactionByIdResponse response = new GetTransactionByIdResponse();
                response.setId(rs.getLong("id"));
                response.setBookerName(rs.getString("user_name"));
                response.setBookerEmail(rs.getString("email"));
                response.setBookerPhoneNumber(rs.getString("phone_number"));
                response.setHotelName(rs.getString("hotel_name"));
                response.setTaxCode(rs.getString("tax_code"));
                response.setAddress(rs.getString("address"));
                response.setPaymentMethod(rs.getString("payment_method_name"));
                response.setAmount(rs.getBigDecimal("amount"));
                response.setAmountSystemReceiver(rs.getBigDecimal("amount").multiply(new BigDecimal(15)).divide(new BigDecimal(100)));
                response.setTransactionTime(rs.getTimestamp("transaction_time"));
                return response;
            } else {
                log.warn("No transaction found with ID: " + id);
            }
        } catch (SQLException e) {
            log.error("Error retrieving transaction by ID: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return null;
    }

    public Integer getTotalTransactionBEOutput(SearchTransactionRequest request, Long staffId) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int totalRecords = 0;
        try {
            String sql = sqlLoader.getSql("COUNT_TRANSACTION_BE_OUTPUT");
            sql = sql.replace(MasterDataConstants.EXTENDS_CONDITION,
                    HandlerUtils.createFullTextSearchQuery(listFieldSearchTransactionBE, request.getSearchParams(), true));

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

    public List<SearchTransactionBEResponse> searchTransactionBE(SearchTransactionRequest request, Long staffId) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<SearchTransactionBEResponse> listResponse = new ArrayList<>();
        String sql = sqlLoader.getSql("SELECT_TRANSACTION_BE");
        sql = sql.replace(MasterDataConstants.EXTENDS_CONDITION,
                HandlerUtils.createFullTextSearchQuery(listFieldSearchTransactionBE, request.getSearchParams(), true));
        StringBuilder pagingQuery = HandlerUtils.appendSortQuery(request.getSearchPaging());
        sql = sql.replace(MasterDataConstants.EXTENDS_PAGING, pagingQuery.toString());
        try {
            List<Object> params = new ArrayList<>();
            params.add(staffId);
            ps = preparedStatement(sql, params);

            rs = ps.executeQuery();

            while (rs.next()) {
                SearchTransactionBEResponse response = new SearchTransactionBEResponse();
                response.setId(rs.getLong("id"));
                response.setCustomerName(rs.getString("user_name"));
                response.setPaymentMethod(rs.getString("payment_method_name"));
                response.setAmount(rs.getBigDecimal("amount"));
                response.setAmountBEReceiver(response.getAmount().multiply(new BigDecimal(85)).divide(new BigDecimal(100)));
                response.setTransactionTime(rs.getTimestamp("transaction_time"));
                int status = rs.getInt("status");
                if(status == TransactionConstants.SUCCESS) {
                    response.setStatus("Hoàn thành");
                } else if(status == TransactionConstants.NOT_FINISH) {
                    response.setStatus("Chưa hoàn thành");
                }
                else if(status == TransactionConstants.PENDING) {
                    response.setStatus("Đang chờ duyệt");
                }
                else if(status == TransactionConstants.REVERSED_TRANSACTION) {
                    response.setStatus("Hoàn trả");
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

    public GetTransactionBEByIdResponse getTransactionBEDetailById(Long id) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = sqlLoader.getSql("SELECT_TRANSACTION_SYSTEM_DETAIL_BY_ID");

        try {
            List<Object> params = new ArrayList<>();
            params.add(id);

            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();

            if (rs.next()) {
                GetTransactionBEByIdResponse response = new GetTransactionBEByIdResponse();
                response.setId(rs.getLong("id"));
                response.setBookerName(rs.getString("user_name"));
                response.setBookerEmail(rs.getString("email"));
                response.setBookerPhoneNumber(rs.getString("phone_number"));
                response.setHotelName(rs.getString("hotel_name"));
                response.setTaxCode(rs.getString("tax_code"));
                response.setAddress(rs.getString("address"));
                response.setPaymentMethod(rs.getString("payment_method_name"));
                response.setAmount(rs.getBigDecimal("amount"));
                response.setAmountBEReceiver(rs.getBigDecimal("amount").multiply(new BigDecimal(85)).divide(new BigDecimal(100)));
                response.setTransactionTime(rs.getTimestamp("transaction_time"));
                return response;
            } else {
                log.warn("No transaction found with ID: " + id);
            }
        } catch (SQLException e) {
            log.error("Error retrieving transaction by ID: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return null;
    }

    public BigDecimal totalRevenueSystem() {
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = sqlLoader.getSql("COUNT_TOTAL_REVENUE_SYSTEM");

        try {
            ps = preparedStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getBigDecimal(1);
            } else {
                log.warn("No transaction found with ID: " );
            }
        } catch (SQLException e) {
            log.error("Error retrieving transaction by ID: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
        return null;
    }

    public List<TotalIncomeResponse.ResponseIncome> totalIncomeSystem() {
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<TotalIncomeResponse.ResponseIncome> listRevenue = new ArrayList<>();

        String sql = sqlLoader.getSql("TOTAL_INCOME_SYSTEM");

        try {
            ps = preparedStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                TotalIncomeResponse.ResponseIncome data = new TotalIncomeResponse.ResponseIncome();
                data.setMonthCode(Month.of(rs.getInt("month")).name());
                data.setMonthNumber(rs.getInt("month"));
                data.setRevenue(rs.getBigDecimal("total_amount"));
                data.setProfit(data.getRevenue().multiply(new BigDecimal(15).divide(new BigDecimal(100))));

                listRevenue.add(data);
            }
            return listRevenue;
        } catch (SQLException e) {
            log.error("Error retrieving transaction by ID: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
        return null;
    }

    public BigDecimal totalRevenueBusinessAdmin(Long businessAdminId) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = sqlLoader.getSql("COUNT_TOTAL_REVENUE_BUSINESS_ADMIN");

            List<Object> params = new ArrayList<>();
            params.add(businessAdminId);

            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            log.error("Error retrieving transaction by ID: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
        return null;
    }

    public List<TotalIncomeResponse.ResponseIncome> totalIncomeBusinessAdmin(Long businessAdminId) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<TotalIncomeResponse.ResponseIncome> listRevenue = new ArrayList<>();

        String sql = sqlLoader.getSql("TOTAL_INCOME_BUSINESS_ADMIN");

        try {
            List<Object> params = new ArrayList<>();
            params.add(businessAdminId);

            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();

            while (rs.next()) {
                TotalIncomeResponse.ResponseIncome data = new TotalIncomeResponse.ResponseIncome();
                data.setMonthCode(Month.of(rs.getInt("month")).name());
                data.setMonthNumber(rs.getInt("month"));
                data.setRevenue(rs.getBigDecimal("total_amount"));
                data.setProfit(data.getRevenue().multiply(new BigDecimal(85).divide(new BigDecimal(100))));

                listRevenue.add(data);
            }
            return listRevenue;
        } catch (SQLException e) {
            log.error("Error retrieving transaction by ID: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
        return null;
    }

    public BigDecimal totalRevenueBusinessOwner(Long businessOwnerId) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = sqlLoader.getSql("COUNT_TOTAL_REVENUE_BUSINESS_OWNER");

            List<Object> params = new ArrayList<>();
            params.add(businessOwnerId);

            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            log.error("Error retrieving transaction by ID: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
        return null;
    }

    public List<TotalIncomeResponse.ResponseIncome> totalIncomeBusinessOwner(Long businessOwnerId) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<TotalIncomeResponse.ResponseIncome> listRevenue = new ArrayList<>();

        String sql = sqlLoader.getSql("TOTAL_INCOME_BUSINESS_OWNER");

        try {
            List<Object> params = new ArrayList<>();
            params.add(businessOwnerId);

            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();

            while (rs.next()) {
                TotalIncomeResponse.ResponseIncome data = new TotalIncomeResponse.ResponseIncome();
                data.setMonthCode(Month.of(rs.getInt("month")).name());
                data.setMonthNumber(rs.getInt("month"));
                data.setRevenue(rs.getBigDecimal("total_amount"));
                data.setProfit(data.getRevenue().multiply(new BigDecimal(85).divide(new BigDecimal(100))));

                listRevenue.add(data);
            }
            return listRevenue;
        } catch (SQLException e) {
            log.error("Error retrieving transaction by ID: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
        return null;
    }
}
