package com.bootravel.payload.responses.data;

import com.bootravel.payload.responses.constant.ResponseType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResponseData<T> extends ResultResponse {
    private T data;
    
    public ResponseData() {
        super("SUCCESS", 200, ResponseType.OK);  // Change "200" to 200
    }

    public ResponseData(T data) {
        super("SUCCESS", 200, ResponseType.OK);  // Change "200" to 200
        this.data = data;
    }

}
