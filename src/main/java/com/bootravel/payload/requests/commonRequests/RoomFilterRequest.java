package com.bootravel.payload.requests.commonRequests;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Date;
import java.util.List;

@AllArgsConstructor
@Data
public class RoomFilterRequest {
    private String userProvinceName;
    private String userHotelName;
    private Integer numberRoom;
    private Integer userMaxPeopleStay;
    private Date userStartDateApply; // Represents the start date for DATE_APPLY
    private Date userEndDateApply;   // Represents the end date for DATE_APPLY
    private Long defaultPriceStart;
    private Long defaultPriceEnd;
    private int limit;
    private int offset;
    public int getLimit() {
        return (limit > 0) ? limit : 10;  // Set a default value of 10 if limit is not provided or is less than or equal to 0
    }

    public int getOffset() {
        return (offset >= 0) ? offset : 0;  // Set a default value of 0 if offset is not provided or is less than 0
    }
}
