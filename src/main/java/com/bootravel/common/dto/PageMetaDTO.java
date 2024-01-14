package com.bootravel.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageMetaDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer pageNum;

    private Integer pageSize;

    private Integer maxPageNum;

    private String sortBy;

    private String sortType;

    private long total;

    private boolean isExceeding;

    public PageMetaDTO(Integer pageNum, Integer pageSize, Integer maxPageNum, String sortBy, String sortType, long total) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.maxPageNum = maxPageNum;
        this.sortBy = sortBy;
        this.sortType = sortType;
        this.total = total;
    }
}