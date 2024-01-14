package com.bootravel.common.constant;

public class MasterDataConstants {

    public static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";

    //Role constant
    public static final long ROLE_ADMIN = 1;

    public static final long ROLE_MARKETING = 2;
    public static final long ROLE_BUSINESS_ADMIN = 3;

    public static final long ROLE_BUSINESS_OWNER = 4;

    public static final long ROLE_BOOKING_STAFF = 5;

    public static final long ROLE_TRANSACTION_STAFF = 6;
    public static final long ROLE_REGISTERED_USER = 7;


    // SETTING DEFAULT PAGING
    public static final Integer DEFAULT_PAGE_SIZE = 10;

    public static final Integer SETTING_DISPLAY_MAX = 3000;

    // SQL constants

    public static final String COMMA = ",";

    public static final String SPACE = " ";

    public static final String OPEN_LEFT = "(";

    public static final String OPEN_RIGHT = ")";

    public static final String SINGLE_QUOTE = "'";

    public static final String SQL_SELECT = "SELECT ";

    public static final String SQL_DELETE = "DELETE";

    public static final String SQL_FROM = " FROM ";

    public static final String SQL_WHERE = " WHERE ";

    public static final String SQL_LIKE = " LIKE ";

    public static final String SQL_ILIKE = " ILIKE ";

    public static final String SQL_ILIKE_OPERATOR = " ~* ";

    public static final String SQL_ORDER_BY = " ORDER BY ";

    public static final String SQL_OFFSET = " OFFSET ";

    public static final String SQL_LIMIT = " LIMIT ";

    public static final String SQL_AND = " AND ";

    public static final String SQL_OR = " OR ";

    public static final String SQL_IN = " IN ";

    public static final String SQL_TYPE_VARCHAR = "VARCHAR";

    public static final String SQL_PERCENT_START = "'%";

    public static final String SQL_PERCENT_END = "%'";

    public static final String VERTICAL_CHAR = "|";

    public static final String EXTENDS_CONDITION = "#EXTENDS_CONDITION#";

    public static final String EXTENDS_PAGING = "#EXTENDS_PAGING#";

    // response status
    public static final String SUCCESS_CODE = "0";

    public static final String ERROR_CODE = "1";

    public static final String SUCCESS_TYPE = "ok";

    public static final String ERROR_TYPE = "error";

    public static final String DOCX_TEMPLATE_PATH = "template/docx/";

    public static final String EMAIL_TEMPLATE_PATH = "template/mail/";

}
