package com.bootravel.utils;

import com.bootravel.common.dto.BaseSearchPagingDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PagingUtils {
    private PagingUtils() {
    }

    public static final Integer DEFAULT_PAGE_SIZE = 10;

    private static final List<String> SORT_TYPES = Arrays.asList("ASC", "DESC");

    public static final String DEFAULT_SORT_TYPE = "ASC";


    public static int settingSearchInfo(BaseSearchPagingDTO searchCondition, List<String> sortColumns, String sortColumn, Integer totalRecord) {
        // Page initialization
        int pageNum = !Objects.isNull(searchCondition.getPageNum()) ? searchCondition.getPageNum() : 0;
        int maxPageNum = getTotalPage(totalRecord, searchCondition.getPageSize());
        if (pageNum < 1) {
            pageNum = 1;
        } else if (maxPageNum > 0 && pageNum > maxPageNum) {
            pageNum = maxPageNum;
        }

        Integer pageSize = searchCondition.getPageSize();
        int offset = (pageNum - 1) * pageSize;
        searchCondition.setPageNum(pageNum);
        searchCondition.setPageSize(pageSize);
        searchCondition.setOffset(offset);

        // Order list check
        if (CollectionUtils.isEmpty(sortColumns) || !sortColumns.contains(searchCondition.getSortBy())) {
            searchCondition.setSortBy(sortColumn);
        }

        // Order setting
        String sortType = searchCondition.getSortType();
        if (StringUtils.isEmpty(sortType) || !SORT_TYPES.contains(sortType.toUpperCase())) {
            searchCondition.setSortType(DEFAULT_SORT_TYPE);
        } else {
            searchCondition.setSortType(sortType.toUpperCase());
        }

        return maxPageNum;
    }

    public static void settingSortWithoutPaging(BaseSearchPagingDTO searchCondition, List<String> sortColumns, String sortColumn, Integer... recordMax) {
        // Order list check
        if (CollectionUtils.isEmpty(sortColumns) || !sortColumns.contains(searchCondition.getSortBy())) {
            searchCondition.setSortBy(sortColumn);
        }

        // Order setting
        String sortType = searchCondition.getSortType();
        if (StringUtils.isEmpty(sortType) || !SORT_TYPES.contains(sortType.toUpperCase())) {
            searchCondition.setSortType(DEFAULT_SORT_TYPE);
        } else {
            searchCondition.setSortType(sortType.toUpperCase());
        }

        // check setting limit data
        if (Objects.nonNull(recordMax) && recordMax.length > 0) {
            searchCondition.setPageSize(recordMax[0]);
        }
    }


    public static int getTotalPage(long totalRecords, Integer pageSize) {
        if (pageSize == 0) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        return (int) Math.ceil((double) totalRecords / pageSize);
    }

    public static int getMaxPageNum(int pageSize, int total) {
        int maxPage = 1;
        if (pageSize != 0 && total > 0) {
            double displayCountValue = pageSize;
            double d = ((double) total) / displayCountValue;
            maxPage = (int) Math.ceil(d);
        }
        return maxPage;
    }
}
