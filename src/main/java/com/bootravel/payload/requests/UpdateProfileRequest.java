package com.bootravel.payload.requests;

import lombok.Data;

import java.util.Date;

@Data
public class UpdateProfileRequest {
    private Long id;
    private String firstName;
    private String lastName;
    private String gender;
    private Date dob;
    private String phoneNumber;
    private Integer provinceId;
    private Integer districtId;
    private Integer wardId;
    private String address1;
    private String address2;
}
