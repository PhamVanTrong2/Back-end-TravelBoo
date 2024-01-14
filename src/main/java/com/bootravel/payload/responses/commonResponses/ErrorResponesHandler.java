package com.bootravel.payload.responses.commonResponses;

import lombok.Data;

@Data
public class ErrorResponesHandler {

    private String message;
    private int statusCode;
    private String errorKey;
    public ErrorResponesHandler(String message,int statusCode ,String errorKey) {
        this.message = message;
        this.statusCode = statusCode;
        this.errorKey = errorKey;
    }
}
