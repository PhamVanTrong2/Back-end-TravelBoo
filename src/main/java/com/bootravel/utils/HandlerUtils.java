package com.bootravel.utils;

import com.bootravel.common.constant.MasterDataConstants;
import com.bootravel.common.dto.BaseSearchPagingDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Method;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HandlerUtils {
    private static Log log = LogFactory.getLog(HandlerUtils.class);

    private HandlerUtils() {
    }

    /**
     * append sort query
     *
     * @param pagingInfo paging info
     * @return sort query
     */
    public static StringBuilder appendSortQuery(BaseSearchPagingDTO pagingInfo) {
        return appendSortQueryDuplicateData(pagingInfo, null);
    }


    /**
     * append sort query
     *
     * @param pagingInfo paging info
     * @return sort query
     */
    public static StringBuilder appendSortQueryDuplicateData(BaseSearchPagingDTO pagingInfo, String listKey) {
        StringBuilder sortQuery = new StringBuilder("");
        if (StringUtils.isEmpty(pagingInfo.getSortBy()) || StringUtils.isEmpty(pagingInfo.getSortType())) {
            return sortQuery;
        }

        // append order by
        sortQuery.append(MasterDataConstants.SQL_ORDER_BY).append(pagingInfo.getSortBy()).append(MasterDataConstants.SPACE).append(pagingInfo.getSortType());

        // append null first, null last
        sortQuery.append(appendSortNull(pagingInfo.getSortBy(), pagingInfo.getSortType()));

        // order by when duplicate data
        if (StringUtils.isNotEmpty(listKey)) {
            sortQuery.append(MasterDataConstants.COMMA).append(MasterDataConstants.SPACE).append(listKey);
        }

        // append offset, limit
        if (!Objects.isNull(pagingInfo.getPageSize())) {
            sortQuery.append(MasterDataConstants.SQL_OFFSET).append(pagingInfo.getOffset());
            sortQuery.append(MasterDataConstants.SQL_LIMIT).append(pagingInfo.getPageSize());
        }

        return sortQuery;
    }

    /**
     * append sort null first/last
     *
     * @param sortBy   sort by
     * @param sortType sort type
     * @return NULL FIRST/ NULL LAST
     */
    public static String appendSortNull(String sortBy, String sortType) {
        if (StringUtils.isNotEmpty(sortBy)) {
            if (sortType.equalsIgnoreCase("ASC")) {
                return " NULLS FIRST";
            }
            return " NULLS LAST";
        }
        return StringUtils.EMPTY;
    }

    /**
     * append sort query
     *
     * @param pagingInfo paging info
     * @return sort query
     */
    public static StringBuilder appendSortQueryIgnoreDataEmpty(BaseSearchPagingDTO pagingInfo, String listKey) {
        StringBuilder sortQuery = new StringBuilder("");
        // order by when duplicate data
        if (StringUtils.isNotEmpty(listKey)) {
            sortQuery.append(MasterDataConstants.COMMA).append(MasterDataConstants.SPACE).append(listKey);
        }

        // append offset, limit
        if (!Objects.isNull(pagingInfo.getPageSize())) {
            sortQuery.append(MasterDataConstants.SQL_OFFSET).append(pagingInfo.getOffset()).append(MasterDataConstants.SPACE);
            sortQuery.append(MasterDataConstants.SQL_LIMIT).append(pagingInfo.getPageSize());
        }

        return sortQuery;
    }


    /**
     * append in condition
     *
     * @param fieldName file name
     * @param values    values
     * @return sql in condition
     */
    public static String appendInCondition(String fieldName, Object[] values) {
        if (StringUtils.isEmpty(fieldName) || values.length == 0) {
            return "";
        }

        StringJoiner inCond = new StringJoiner(
                MasterDataConstants.COMMA,
                fieldName + MasterDataConstants.SQL_IN + MasterDataConstants.OPEN_LEFT,
                MasterDataConstants.OPEN_RIGHT);
        for (Object value : values) {
            if (value instanceof Integer || value instanceof Long) {
                inCond.add(value + StringUtils.EMPTY);
            } else {
                inCond.add(MasterDataConstants.SINGLE_QUOTE + value + MasterDataConstants.SINGLE_QUOTE);
            }
        }

        return inCond.toString();
    }


    private static <T> void excuteInvoke(ResultSet resultSet, T objMapTarget, Method method, String fieldName, Class<?> parameterType) {
        try {
            Object value = null;

            // check value is null
            resultSet.getString(fieldName);
            if (!resultSet.wasNull()) {
                if (Integer.class.equals(parameterType)) {                           // type Integer
                    value = resultSet.getInt(fieldName);
                } else if (Long.class.equals(parameterType)) {                       // type Long
                    value = resultSet.getLong(fieldName);
                } else if (Double.class.equals(parameterType)) {                     // type Double
                    value = resultSet.getDouble(fieldName);
                } else if (Boolean.class.equals(parameterType)) {                    // type Boolean
                    value = resultSet.getBoolean(fieldName);
                } else if (Date.class.equals(parameterType)) {                       // type Date
                    value = resultSet.getDate(fieldName);
                } else if (String.class.equals(parameterType)) {                      // type String
                    value = resultSet.getString(fieldName);
                }
            } else { // set default value blank when parameterType is String
                if (String.class.equals(parameterType)) {
                    value = StringUtils.EMPTY;
                }
            }
            // excute method
            method.invoke(objMapTarget, value);

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    public static String createMultiValueSearch(String keywords, Boolean... searchLikeOpt) {
        if (StringUtils.isNotEmpty(keywords)) {

            // search like flag
            boolean isSearchLike = Objects.nonNull(searchLikeOpt) && searchLikeOpt.length > 0 && searchLikeOpt[0];

            // create value
            String[] texts = keywords.split(",");

            StringBuilder output = new StringBuilder();

            for (int i = 0; i < texts.length; i++) {
                output.append(MasterDataConstants.SQL_PERCENT_START).append(texts[i]).append(MasterDataConstants.SQL_PERCENT_END);
                if (i < texts.length - 1) {
                    output.append(",");
                }
            }

            return String.valueOf(output);
        }
        return StringUtils.EMPTY;
    }

    public static StringBuilder createFullTextSearchQuery(List<String> fullTextFields, String txbKeyword, Boolean... searchLikeOpt) {
        StringBuilder fullTextQuery = new StringBuilder();
        fullTextQuery.append(MasterDataConstants.SQL_AND);
        if (StringUtils.isEmpty(txbKeyword) || StringUtils.isEmpty(txbKeyword)) {
            return new StringBuilder();
        }

        // get search like opt
        boolean isSearchLike = Objects.nonNull(searchLikeOpt) && searchLikeOpt.length > 0 && searchLikeOpt[0];

        // check if search like
        txbKeyword = isSearchLike ? txbKeyword.toLowerCase() : txbKeyword;

        // create query multiple with fields
        String finalTxbKeyword = createMultiValueSearch(txbKeyword, searchLikeOpt);
        String query = fullTextFields.stream()
                .map(item -> {
                    if (isSearchLike) {
                        return "LOWER(" + item + ")" + " LIKE ANY(ARRAY[" + finalTxbKeyword + "])";
                    }
                    return item + MasterDataConstants.SQL_ILIKE_OPERATOR + MasterDataConstants.SINGLE_QUOTE + finalTxbKeyword
                            + MasterDataConstants.SINGLE_QUOTE;
                }).collect(Collectors.joining(MasterDataConstants.SQL_OR));
        fullTextQuery.append(MasterDataConstants.OPEN_LEFT).append(query).append(MasterDataConstants.OPEN_RIGHT).append(MasterDataConstants.SPACE);

        return fullTextQuery;
    }
}

