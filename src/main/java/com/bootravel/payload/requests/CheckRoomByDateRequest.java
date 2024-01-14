package com.bootravel.payload.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
@AllArgsConstructor
@Data
@NoArgsConstructor
public class CheckRoomByDateRequest {
    private Integer numberRoom;
    private Integer userMaxPeopleStay;
    private Date userStartDateApply; // Represents the start date for DATE_APPLY
    private Date userEndDateApply;
}
