package com.bootravel.payload.responses;

import lombok.Data;

import java.sql.Date;

@Data
public class SearchPromotionResponse {
    private Long id;
    private String code;
    private String name;
    private Date startDate;
    private Date endDate;
    private String status;
    private String imageUrl;
}
