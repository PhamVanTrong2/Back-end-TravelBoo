package com.bootravel.payload.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetRoomAvailableByDateRequest {
    private Long roomId;
    private String dateFrom;
    private String dateTo;
    private Long numRoomAvailable;
}
