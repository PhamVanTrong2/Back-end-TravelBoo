package com.bootravel.payload.responses.commonResponses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private Long roleId;

    public LoginResponse(Long roleId) {
        this.roleId = roleId;
    }}
