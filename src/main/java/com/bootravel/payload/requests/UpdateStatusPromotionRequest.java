package com.bootravel.payload.requests;

import lombok.Data;

@Data
public class UpdateStatusPromotionRequest {
    private Long id;
    private String status;
}
