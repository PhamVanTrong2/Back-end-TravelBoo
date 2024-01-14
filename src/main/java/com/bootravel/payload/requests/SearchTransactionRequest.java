package com.bootravel.payload.requests;

import com.bootravel.common.dto.BaseSearchPagingDTO;
import lombok.Data;

@Data
public class SearchTransactionRequest {
    private String searchParams;
    private BaseSearchPagingDTO searchPaging = new BaseSearchPagingDTO();
}
