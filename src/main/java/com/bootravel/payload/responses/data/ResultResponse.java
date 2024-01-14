package com.bootravel.payload.responses.data;

import com.bootravel.payload.responses.constant.ResponseType;
import lombok.Data;

import java.io.Serializable;

@Data
public class ResultResponse implements Serializable {

    public static final String SUCCESS = "SUCCESS";

    private String message;

    private int code;

    private int totalCount;

    private ResponseType type;

    public ResultResponse() {
        this.message = "";
        this.code = 200;
        this.type = ResponseType.OK;
    }

    public ResultResponse(String message, int code, ResponseType type) {
        this.message = message;
        this.code = code;
        this.type = type;
    }
    public ResultResponse(String message, int code,int totalCount, ResponseType type) {
        this.message = message;
        this.totalCount = totalCount;
        this.code = code;
        this.type = type;
    }
    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
