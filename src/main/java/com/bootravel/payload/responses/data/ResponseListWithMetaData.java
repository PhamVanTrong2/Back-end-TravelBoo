package com.bootravel.payload.responses.data;

import com.bootravel.common.constant.MasterDataConstants;
import com.bootravel.common.dto.PageMetaDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;

@Data
@NoArgsConstructor
@JsonPropertyOrder({ "type", "message", "code", "data" })
public class ResponseListWithMetaData<T> {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Response Type
     */
    private String type;

    /**
     * Response Message
     */
    @JsonInclude
    private String message;

    /**
     * Response Code
     */
    private String code;

    /**
     * init data
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object init;

    /**
     * Response data
     */
    private Collection<T> data;

    /**
     * Paging Handle Ignore on result if null
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PageMetaDTO meta;

    /**
     * set data info
     * @param data data
     * @return response
     */
    public ResponseListWithMetaData<T> setData(Collection<T> data) {
        this.type = MasterDataConstants.SUCCESS_TYPE;
        this.code = MasterDataConstants.SUCCESS_CODE;
        this.data = data;
        return this;
    }

    /**
     * set success response
     * @param meta meta data
     * @param data data
     */
    public void setSuccessResponse(PageMetaDTO meta, Collection<T> data) {
        this.type = MasterDataConstants.SUCCESS_TYPE;
        this.code = MasterDataConstants.SUCCESS_CODE;
        this.data = data;
        this.meta = meta;
    }

    /**
     * set error response
     * @param message message
     */
    public void setErrorResponse(String message) {
        this.type = MasterDataConstants.ERROR_TYPE;
        this.code = MasterDataConstants.ERROR_CODE;
        this.message = message;
    }

    /**
     * set error response
     * @param message message
     */
    public void setErrorResponse(Collection<T> data, String...message) {
        this.type = MasterDataConstants.ERROR_TYPE;
        this.code = MasterDataConstants.ERROR_CODE;
        this.data = data;
        if(!Objects.isNull(message) && message.length > 0) {
            this.message = message[0];
        }
    }
}