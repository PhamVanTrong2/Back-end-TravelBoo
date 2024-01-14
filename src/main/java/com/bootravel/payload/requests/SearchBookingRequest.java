package com.bootravel.payload.requests;

import com.bootravel.common.dto.BaseSearchPagingDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchBookingRequest {
    private String searchParams;
    private BaseSearchPagingDTO searchPaging = new BaseSearchPagingDTO();
}
