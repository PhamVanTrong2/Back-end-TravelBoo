package com.bootravel.payload.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeachBoByManagerResponse {

    private Long id;
    private String fullName;
}
