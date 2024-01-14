package com.bootravel.payload.requests;

import com.bootravel.common.dto.BaseSearchPagingDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class SearchUserRequest {
    private String searchParams;
    private BaseSearchPagingDTO searchPaging = new BaseSearchPagingDTO();


}
