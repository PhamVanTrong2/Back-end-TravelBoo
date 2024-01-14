package com.bootravel.payload.responses;

import lombok.Data;

@Data
public class PostData {
    private String vnp_RequestId;
    private String vnp_Version;
    private String vnp_Command;
    private String vnp_TmnCode;
    private String vnp_TxnRef;
    private String vnp_OrderInfo;
    // Add other fields as needed
    // Create getters and setters for each field
}
