package com.bootravel.payload.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BedRoomInfo {
    private Integer countBed;
    private String bedTypeName;

    private Long bedTypeId;
}
