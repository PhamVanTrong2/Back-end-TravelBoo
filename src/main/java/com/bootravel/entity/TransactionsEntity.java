package com.bootravel.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class TransactionsEntity {
    private Long id;
    private Integer userId;
    private Integer paymentMethodId;
    private BigDecimal amount;
    private Timestamp transactionTime;
    private Integer bookingRoomId;
    private Integer status;
}
