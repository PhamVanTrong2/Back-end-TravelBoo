package com.bootravel.exception;

import com.bootravel.common.constant.MasterDataConstants;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Getter
@Setter
public class CommonException extends Exception {

    private static final long serialVersionUID = -1685051354729837553L;

    private String message;

    private String code;

    private String type;
    private final String[] params;

    public void setType(String type) {
        this.type = type;
    }

    public CommonException() {
        this(StringUtils.EMPTY);
    }

    public CommonException(String msg) {
        super(msg);
        this.type = MasterDataConstants.ERROR_TYPE;
        this.message = msg;
        this.code = "500";
        params = null;
    }

    public CommonException(String errorCode, List<String> params) {
        this.type = MasterDataConstants.ERROR_TYPE;
        this.code = errorCode;
        this.params = params.toArray(new String[0]);
    }
    public CommonException(String errorCode, String... params) {
        this.type = MasterDataConstants.ERROR_TYPE;
        this.code = errorCode;
        this.params = params;
    }
}
