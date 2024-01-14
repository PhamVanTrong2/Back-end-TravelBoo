package com.bootravel.payload.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuggestHotelResponse {
    private Long hotelId;
    private String hotelName;
    private String province;
    private Long numberBooked;
    private String imageUrl;
}
