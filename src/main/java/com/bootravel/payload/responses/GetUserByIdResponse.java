package com.bootravel.payload.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
public class GetUserByIdResponse {
    private Long id;
    private String userName;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Date birthDate;
    private String gender;
    private String avatar;
    private Boolean status;
    private Long roleId;
    private String address1;
    private String address2;
    private Long addressId;
    private Long wardId;
    private Long districtId;
    private Long provinceId;
    private String wardName;
    private String districtName;
    private String provinceName;
    private String identification;
}
