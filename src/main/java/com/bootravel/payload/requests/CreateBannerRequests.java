package com.bootravel.payload.requests;

import lombok.Data;

import java.time.Instant;

@Data
public class CreateBannerRequests {

    private String images;


    private Boolean status;


    private String types;


    private Integer hotelId;


    private Integer createdBy;


    private Instant createdDate;


    private Instant lastModifyDate;
}
