package com.bootravel.payload.requests;

import lombok.Data;

import java.util.Date;

@Data
public class CreateBaRequest {
    private String userName;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Date dateOfBirth;
    private String gender;
    private long wardId;
    private String address1;
    private String address2;
    private String identification;
}
