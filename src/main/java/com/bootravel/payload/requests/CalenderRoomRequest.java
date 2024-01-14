package com.bootravel.payload.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalenderRoomRequest {

    private Date dateFrom;

    private Date dateTo;

    private Long roomId;
}
