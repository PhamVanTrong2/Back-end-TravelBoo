package com.bootravel.payload.requests;

import lombok.Data;

@Data
public class UpdateStatusRequest {
    private Long id;

    private Boolean status;
}
