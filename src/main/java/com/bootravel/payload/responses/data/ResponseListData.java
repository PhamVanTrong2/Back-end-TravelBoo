package com.bootravel.payload.responses.data;

import com.bootravel.payload.responses.constant.ResponseType;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class ResponseListData<T> extends ResultResponse {
    private Collection<T> listData;

    public ResponseListData() {
        super("SUCCESS", 200, ResponseType.OK);
    }

    public ResponseListData(Collection<T> listData, ResultResponse resultResponse) {
        super("SUCCESS", 200,resultResponse.getTotalCount(), ResponseType.OK);
        this.listData = listData;
    }


}
