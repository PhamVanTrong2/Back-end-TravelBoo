package com.bootravel.payload.requests;

import com.bootravel.common.dto.BaseSearchPagingDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@Data
@NoArgsConstructor
public class SearchRoomRequest {
    private String searchParams;

    private BaseSearchPagingDTO baseSearchPagingDTO = new BaseSearchPagingDTO();
}
