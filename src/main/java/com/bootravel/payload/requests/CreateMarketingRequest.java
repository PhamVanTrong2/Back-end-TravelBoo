package com.bootravel.payload.requests;

import lombok.Data;

import java.util.Date;

@Data
public class CreateMarketingRequest {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Date birthDate;
    private String gender;
    private long wardId;
    private String address1;
    private String address2;
}
