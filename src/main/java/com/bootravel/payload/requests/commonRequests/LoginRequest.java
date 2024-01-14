package com.bootravel.payload.requests.commonRequests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String userName;
    private String userPassword;
}
