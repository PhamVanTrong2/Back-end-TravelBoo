package com.bootravel.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@Data
public class BaseSearchPagingDTO implements Serializable {


    private static final long serialVersionUID = 1L;

    protected Integer pageNum = 0;

    protected Integer offset = 0;

    protected Integer pageSize;

    protected String sortBy;

    protected String sortType;


    public BaseSearchPagingDTO(Integer pageNum, Integer pageSize, String sortBy, String sortType, Integer... isSelectAll) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.sortBy = sortBy;
        this.sortType = sortType;

    }

    public BaseSearchPagingDTO(Integer pageNum, Integer offset, Integer pageSize, String sortBy, String sortType) {
        this.pageNum = pageNum;
        this.offset = offset;
        this.pageSize = pageSize;
        this.sortBy = sortBy;
        this.sortType = sortType;
    }
}
