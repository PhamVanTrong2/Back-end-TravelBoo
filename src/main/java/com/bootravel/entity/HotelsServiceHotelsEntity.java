package com.bootravel.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class HotelsServiceHotelsEntity {
    private Long id;
    private Boolean status;
    private Integer hotelId;
    private Integer hotelServiceId;
}
