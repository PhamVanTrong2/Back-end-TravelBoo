package com.bootravel.service;

import com.bootravel.common.dto.BaseSearchPagingDTO;
import com.bootravel.common.dto.PageMetaDTO;
import com.bootravel.common.security.jwt.dto.CustomUserDetails;
import com.bootravel.entity.TransactionsEntity;
import com.bootravel.payload.requests.SearchTransactionRequest;
import com.bootravel.payload.responses.GetTransactionBEByIdResponse;
import com.bootravel.payload.responses.GetTransactionByIdResponse;
import com.bootravel.payload.responses.SearchTransactionBEResponse;
import com.bootravel.payload.responses.SearchTransactionSystemResponse;
import com.bootravel.payload.responses.data.ResponseData;
import com.bootravel.payload.responses.data.ResponseListWithMetaData;
import com.bootravel.repository.TransactionsRepository;
import com.bootravel.service.common.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@Transactional
public class TransactionService {

    private static final String DEFAULT_SORT = "id";

    private static final List<String> HEADER_SORT = Arrays.asList("transactionTime");

    @Autowired
    private CommonService commonService;

    @Autowired
    private TransactionsRepository transactionsRepository;
    public TransactionsEntity updateStatusTransaction(Long transactionId, Integer newStatus){
        return transactionsRepository.updateTransactionStatus(transactionId,newStatus);
    }

    public ResponseListWithMetaData<SearchTransactionSystemResponse> searchTransactionSystem(SearchTransactionRequest request) {
        if (Objects.isNull(request)) {
            request = new SearchTransactionRequest();
        }
        ResponseListWithMetaData<SearchTransactionSystemResponse> responseListData = new ResponseListWithMetaData<>();
        Integer totalRecords = transactionsRepository.getTotalTransactionSystemOutput(request);

        BaseSearchPagingDTO pagingDTO = request.getSearchPaging();
        PageMetaDTO meta = commonService.settingPageMetaInfo(request.getSearchPaging(),
                StringUtils.isEmpty(pagingDTO.getSortBy()) ? HEADER_SORT : Collections.singletonList(pagingDTO.getSortBy()),
                DEFAULT_SORT, totalRecords);

        List<SearchTransactionSystemResponse> listUsers = transactionsRepository.searchTransactionSystem(request);
        responseListData.setSuccessResponse(meta, listUsers);
        return responseListData;
    }

    public ResponseData<GetTransactionByIdResponse> getTransactionById(Long id) {
        if (id == null) return null;
        GetTransactionByIdResponse data = transactionsRepository.getTransactionDetailById(id);
        ResponseData<GetTransactionByIdResponse> responseData = new ResponseData<>();
        if(data == null) {
            responseData.setCode(404);
        } else {
            responseData.setData(data);
        }
        return responseData;
    }

    public ResponseListWithMetaData<SearchTransactionBEResponse> searchTransactionBE(SearchTransactionRequest request) {
        if (Objects.isNull(request)) {
            request = new SearchTransactionRequest();
        }

        CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ResponseListWithMetaData<SearchTransactionBEResponse> responseListData = new ResponseListWithMetaData<>();
        Integer totalRecords = transactionsRepository.getTotalTransactionBEOutput(request, principal.getId());

        BaseSearchPagingDTO pagingDTO = request.getSearchPaging();
        PageMetaDTO meta = commonService.settingPageMetaInfo(request.getSearchPaging(),
                StringUtils.isEmpty(pagingDTO.getSortBy()) ? HEADER_SORT : Collections.singletonList(pagingDTO.getSortBy()),
                DEFAULT_SORT, totalRecords);

        List<SearchTransactionBEResponse> listData = transactionsRepository.searchTransactionBE(request, principal.getId());
        responseListData.setSuccessResponse(meta, listData);
        return responseListData;
    }

    public ResponseData<GetTransactionBEByIdResponse> getTransactionBEById(Long id) {
        if (id == null) return null;
        GetTransactionBEByIdResponse data = transactionsRepository.getTransactionBEDetailById(id);
        ResponseData<GetTransactionBEByIdResponse> responseData = new ResponseData<>();
        if(data == null) {
            responseData.setCode(404);
        } else {
            responseData.setData(data);
        }
        return responseData;
    }
}
