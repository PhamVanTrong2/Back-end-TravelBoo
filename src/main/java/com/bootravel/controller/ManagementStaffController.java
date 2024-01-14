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
import com.bootravel.service.ManagementStaffService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Null;

@RestController
@Slf4j
@RequestMapping("management-staff")
public class ManagementStaffController {

    @Autowired
    private ManagementStaffService managementStaffService;

    @PostMapping("/create-staff")
    public ResultResponse createStaff(@RequestBody CreateStaffRequest request) throws Exception {
        return managementStaffService.createStaff(request);
    }

    @PostMapping("/update-status")
    public ResultResponse updateStaff(@RequestBody UpdateStatusRequest request) throws Exception {
        return managementStaffService.updateStatus(request);
    }

    @GetMapping("/get-staff/{id}")
    public ResponseData<GetUserByIdResponse> getStaffById(@PathVariable("id") Long id) throws Exception {
        return managementStaffService.getStaffById(id);
    }

    @PostMapping("/search-staff")
    public ResponseListWithMetaData<UsersEntity> searchStaff(@RequestBody @Null SearchUserRequest searchUserRequest) throws Exception {
        return managementStaffService.searchListStaff(searchUserRequest);
    }
}