package com.bootravel.controller;


import com.bootravel.entity.UsersEntity;
import com.bootravel.payload.requests.CreateBaRequest;
import com.bootravel.payload.requests.SearchUserRequest;
import com.bootravel.payload.requests.UpdateStatusRequest;
import com.bootravel.payload.responses.GetUserByIdResponse;
import com.bootravel.payload.responses.data.ResponseData;
import com.bootravel.payload.responses.data.ResponseListWithMetaData;
import com.bootravel.payload.responses.data.ResultResponse;
import com.bootravel.service.BusinessAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Null;

@RestController
@Slf4j
@RequestMapping("business-admin")
public class ManagementBaController {

    @Autowired
    private BusinessAdminService businessAdminService;

    @PostMapping("/create-business-admin")
    public ResultResponse createBusinessAdmin(@RequestBody CreateBaRequest request) throws Exception {
        return businessAdminService.createBa(request);
    }

    @PostMapping("/search-business-admin")
    public ResponseListWithMetaData<UsersEntity> searchBusinessAdmin(@RequestBody @Null SearchUserRequest searchUserRequest) throws Exception {
        return businessAdminService.searchListBa(searchUserRequest);
    }

    @PostMapping("/update-status")
    public ResultResponse updateBusinessAdmin(@RequestBody UpdateStatusRequest request) throws Exception {
        return businessAdminService.updateStatusBa(request);
    }

    @GetMapping("/get-ba/{id}")
    public ResponseData<GetUserByIdResponse> getBusinessAdminById(@PathVariable("id") Long id) throws Exception {
        return businessAdminService.getBaById(id);
    }

}
