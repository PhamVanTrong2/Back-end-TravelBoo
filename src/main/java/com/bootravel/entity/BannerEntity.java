package com.bootravel.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.sql.Timestamp;


@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class BannerEntity   {
    private Long id;
    private String images;
    private Boolean status;
    private String types;
    private Integer hotelId;
    private Integer createdBy;
    private Timestamp createdDate;
    private Timestamp lastModifyDate;
}
