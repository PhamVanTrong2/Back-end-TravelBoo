package com.bootravel.payload.responses.commonResponses;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class BookingTransactionResponse {
    private Long bookingId;
    private Long transactionId;
    private String vnPayResponse;
}
