package com.bootravel.exception;

public class FieldAlreadyUsedException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public FieldAlreadyUsedException() {
        super(ErrorConstants.USER_NAME_ALREADY_USED_TYPE, "User name or email,phone is already in use!", "userManagement", "exists");
    }
}
