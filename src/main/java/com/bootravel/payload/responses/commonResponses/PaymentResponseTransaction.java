package com.bootravel.payload.responses.commonResponses;

import com.bootravel.payload.responses.PostData;
import lombok.Data;

import java.io.Serializable;

@Data
public class PaymentResponseTransaction implements Serializable {
    private int statusCode;
    private PostData postData;
    private String URL;
}
