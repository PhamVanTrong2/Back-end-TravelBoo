package com.bootravel.payload.responses;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuggestLocationResponse {
    private Long provinceId;
    private String provinceName;
    private Long numberBooked;
    private String imageUrl;
}
