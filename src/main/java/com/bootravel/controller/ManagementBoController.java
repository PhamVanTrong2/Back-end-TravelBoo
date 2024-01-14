package com.bootravel.controller;


import com.bootravel.common.security.jwt.dto.CustomUserDetails;
import com.bootravel.entity.UsersEntity;
import com.bootravel.payload.requests.CreateStaffRequest;
import com.bootravel.payload.requests.SearchUserRequest;
import com.bootravel.payload.requests.UpdateStatusRequest;
import com.bootravel.payload.responses.GetUserByIdResponse;
import com.bootravel.payload.responses.SeachBoByManagerResponse;
import com.bootravel.payload.responses.data.ResponseData;
import com.bootravel.payload.responses.data.ResponseListData;
import com.bootravel.payload.responses.data.ResponseListWithMetaData;
import com.bootravel.payload.responses.data.ResultResponse;
import com.bootravel.service.BusinessOwnerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Null;

@RestController
@Slf4j
@RequestMapping("business-owner")
public class ManagementBoController {

    @Autowired
    private BusinessOwnerService businessOwnerService;

    @PostMapping("create-bo")
    public ResultResponse createBusinessOwner(@RequestBody CreateStaffRequest request) throws Exception {
        return businessOwnerService.createBo(request);
    }

    @PostMapping("/update-status")
    public ResultResponse updateBusinessOwner(@RequestBody UpdateStatusRequest request) throws Exception {
        return businessOwnerService.updateStatusBo(request);
    }

    @GetMapping("/get-bo/{id}")
    public ResponseData<GetUserByIdResponse> getBusinessOwnerById(@PathVariable("id") Long id) throws Exception {
        return businessOwnerService.getBoById(id);
    }

    @PostMapping("/search-business-owner")
    public ResponseListWithMetaData<UsersEntity> searchBusinessOwner(@RequestBody @Null SearchUserRequest searchUserRequest) throws Exception {
        return businessOwnerService.searchListBo(searchUserRequest);
    }

    @PostMapping("search-bo-by-manager")
    public ResponseListData<SeachBoByManagerResponse> seachBoByManager() throws Exception {
        return businessOwnerService.seachBoByManager();
    }
}
