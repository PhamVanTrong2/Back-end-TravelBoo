package com.bootravel.payload.responses.commonResponses;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.batik.gvt.event.SelectionListener;

import java.io.Serializable;
@Getter
@Setter
public class PaymentResponse implements Serializable {
    private String status;
    private String message;
    private String URL;
    private Long bookingId;
    private Long transactionId;

}
