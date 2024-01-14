package com.bootravel.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class RoomAvailableByDatesEntity {
    private Long id;
    private Long numberRoomAvailable;
    private Date dateApply;
    private Integer roomId;
    private Long totalRoomBooking;
}
