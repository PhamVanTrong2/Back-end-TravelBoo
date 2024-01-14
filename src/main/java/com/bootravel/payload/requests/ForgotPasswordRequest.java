package com.bootravel.payload.requests;

import lombok.Data;

@Data
public class ForgotPasswordRequest {
    private String email;
}
