package com.bootravel.payload.requests;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomRequestVerTwo {
    @NotNull
    private long id;

    @ApiParam(value = "Start date in yyyy-MM-dd format", example = "2023-01-01")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateFrom;

    @ApiParam(value = "End date in yyyy-MM-dd format", example = "2023-01-10")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateTo;

    @ApiParam(value = "Number of rooms")
    private Integer numberRoom;

    @ApiParam(value = "Number of people")
    private Integer numberPeople;
}
