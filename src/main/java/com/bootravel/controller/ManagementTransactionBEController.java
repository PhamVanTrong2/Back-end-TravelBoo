package com.bootravel.controller;

import com.bootravel.payload.requests.SearchTransactionRequest;
import com.bootravel.payload.responses.GetTransactionBEByIdResponse;
import com.bootravel.payload.responses.SearchTransactionBEResponse;
import com.bootravel.payload.responses.data.ResponseData;
import com.bootravel.payload.responses.data.ResponseListWithMetaData;
import com.bootravel.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Null;

@RestController
@Slf4j
@RequestMapping("transaction-be")
public class ManagementTransactionBEController {
    @Autowired
    private TransactionService transactionService;

    @PostMapping("/search-transaction")
    public ResponseListWithMetaData<SearchTransactionBEResponse> searchTransactionBE(@RequestBody @Null SearchTransactionRequest request) {
        return transactionService.searchTransactionBE(request);
    }

    @GetMapping("/get-transaction/{id}")
    public ResponseData<GetTransactionBEByIdResponse> getTransactionById(@PathVariable Long id) {
        return transactionService.getTransactionBEById(id);
    }
}
