package com.bootravel.payload.requests.commonRequests;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;

@Data
@Getter
@Setter
public class HotelFilterRequest {
    private String search;
    private Integer numberRoom;
    private Integer numberPeople;
    private Integer hotelStar;
    private List<Integer> rangePrice;
    private Date dateFrom; // Represents the start date for DATE_APPLY
    private Date dateTo;
    @Nullable
    private ServiceList serviceList;
    private int limit;
    private int offset;
    public int getLimit() {
        return (limit > 0) ? limit : 10;  // Set a default value of 10 if limit is not provided or is less than or equal to 0
    }

    public int getOffset() {
        return (offset >= 0) ? offset : 0;  // Set a default value of 0 if offset is not provided or is less than 0
    }

    public String sort;

    @Data
    @Getter
    @Setter
    public static class ServiceList {
        private Boolean includesBreakfast;
    }


    public String getSortBySql() {
        switch (sort) {
            case "star":
                return "ORDER BY H.HOTEL_STAR DESC";
            case "minPrice":
                return "ORDER BY FINAL_PRICE ASC";
            case "maxPrice":
                return "ORDER BY FINAL_PRICE DESC";
            default:
                return "";
        }
    }
}
