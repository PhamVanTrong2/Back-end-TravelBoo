package com.bootravel.controller;

import com.bootravel.entity.UsersEntity;
import com.bootravel.payload.requests.CreateMarketingRequest;
import com.bootravel.payload.requests.SearchUserRequest;
import com.bootravel.payload.requests.UpdateStatusRequest;
import com.bootravel.payload.requests.commonRequests.MailsRequests;
import com.bootravel.payload.responses.GetUserByIdResponse;
import com.bootravel.payload.responses.data.ResponseData;
import com.bootravel.payload.responses.data.ResponseListWithMetaData;
import com.bootravel.payload.responses.data.ResultResponse;
import com.bootravel.service.ManagementMarketingService;
import com.bootravel.service.common.EmailService;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Email;
import javax.validation.constraints.Null;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

@RestController
@Slf4j
@RequestMapping("/marketing")
public class ManagementMarketingController {

    @Autowired
    private ManagementMarketingService managementMarketingService;

    @PostMapping("/create-marketing")
    public ResultResponse createMarketing(@RequestBody CreateMarketingRequest request) throws Exception {
        return managementMarketingService.createMarketing(request);
    }

    @PostMapping("/update-status")
    public ResultResponse updateMarketing(@RequestBody UpdateStatusRequest request) throws Exception {
        return managementMarketingService.updateMarketing(request);
    }

    @GetMapping("/get-marketing/{id}")
    public ResponseData<GetUserByIdResponse> getMarketingById(@PathVariable("id") Long id) throws Exception {
        return managementMarketingService.getMarketingById(id);
    }

    @PostMapping("/search-marketing")
    public ResponseListWithMetaData<UsersEntity> searchMarketing(@RequestBody @Null SearchUserRequest searchUserRequest) throws Exception {
        return managementMarketingService.searchListMarketing(searchUserRequest);
    }

}
