package com.bootravel.controller;

import com.bootravel.payload.requests.SearchTransactionRequest;
import com.bootravel.payload.responses.GetTransactionByIdResponse;
import com.bootravel.payload.responses.SearchTransactionSystemResponse;
import com.bootravel.payload.responses.data.ResponseData;
import com.bootravel.payload.responses.data.ResponseListWithMetaData;
import com.bootravel.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Null;

@RestController
@Slf4j
@RequestMapping("transaction-system")
public class ManagementTransactionSystemController {
    @Autowired
    private TransactionService transactionService;

    @PostMapping("/search-transaction")
    public ResponseListWithMetaData<SearchTransactionSystemResponse> searchTransactionSystem(@RequestBody @Null SearchTransactionRequest request) throws Exception {
        return transactionService.searchTransactionSystem(request);
    }

    @GetMapping("/get-transaction/{id}")
    public ResponseData<GetTransactionByIdResponse> getTransactionById(@PathVariable Long id) {
        return transactionService.getTransactionById(id);
    }
}
