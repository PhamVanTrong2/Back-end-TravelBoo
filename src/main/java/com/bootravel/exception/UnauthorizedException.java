package com.bootravel.exception;

import java.net.URI;

public class UnauthorizedException  extends BadRequestAlertException{
    private static final long serialVersionUID = 1L;

    public UnauthorizedException() {
        super(ErrorConstants.USER_NAME_ALREADY_USED_TYPE, "User name or email, phone is invalid!", "userManagement", "invalid");
    }
}
