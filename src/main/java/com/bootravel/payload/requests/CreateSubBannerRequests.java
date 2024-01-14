package com.bootravel.payload.requests;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CreateSubBannerRequests {
    private String types;
    private Integer hotelId;
}
