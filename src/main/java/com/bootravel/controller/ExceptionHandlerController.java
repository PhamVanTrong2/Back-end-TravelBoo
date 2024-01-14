package com.bootravel.controller;

import com.bootravel.common.security.jwt.exception.TokenRefreshException;
import com.bootravel.payload.responses.data.ResultResponse;
import com.bootravel.payload.responses.constant.ResponseType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ExceptionHandlerController {

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ResultResponse> handleConflict(Exception e) {
        log.info(e.getMessage(), e);
        return handleExceptionInternal(e);
    }

    private ResponseEntity<ResultResponse> handleExceptionInternal(Exception e) {
        ResultResponse response = new ResultResponse();
        response.setCode(500);
        response.setType(ResponseType.ERROR);
        response.setMessage(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(value = TokenRefreshException.class)
    public ResponseEntity<ResultResponse> handleTokenRefreshException(Exception e) {
        log.info(e.getMessage(), e);
        return handleExceptionInternal(e);
    }
}
