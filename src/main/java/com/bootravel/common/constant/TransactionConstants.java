package com.bootravel.common.constant;

public class TransactionConstants {
    public static final Integer SUCCESS = 0; // Giao dịch thanh toán thành công
    public static final Integer NOT_FINISH = 1; // Giao dịch chưa hoàn tất
    public static final Integer REVERSED_TRANSACTION = 4; // Giao dịch đảo (Khách hàng đã bị trừ tiền tại Ngân hàng nhưng GD chưa thành công ở VNPAY)
    public static final Integer PENDING = 2; // Giao dịch thanh toán thành công

}
