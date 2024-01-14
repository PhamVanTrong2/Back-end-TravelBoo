package com.bootravel.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class RoomPriceByDatesEntity {
    private Long id;
    private BigDecimal price;
    private Date dateApply;
    private Integer roomId;
}
