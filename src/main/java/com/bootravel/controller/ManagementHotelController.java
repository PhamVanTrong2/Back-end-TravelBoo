package com.bootravel.controller;


import com.bootravel.common.security.jwt.dto.CustomUserDetails;

import com.bootravel.payload.requests.CreateHotelRequest;
import com.bootravel.payload.requests.SearchHotelRequest;

import com.bootravel.payload.requests.UpdateHotelRequest;
import com.bootravel.payload.requests.UpdateStatusRequest;
import com.bootravel.payload.responses.GetHotelResponseVerTwo;
import com.bootravel.payload.responses.SearchHotelResponse;
import com.bootravel.payload.responses.data.ResponseData;
import com.bootravel.payload.responses.data.ResponseListWithMetaData;
import com.bootravel.payload.responses.data.ResultResponse;
import com.bootravel.service.HotelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.validation.constraints.Null;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/hotel")
public class ManagementHotelController {

    @Autowired
    private HotelService hotelService;

    @PostMapping(value = "/create-hotel")
    public ResultResponse createHotel(
            @RequestParam("name") String name,
            @RequestParam("star") Integer star,
            @RequestParam("description") String description,
            @RequestParam("note") String note,
            @RequestParam("businessOwner") Long businessOwner,
            @RequestParam("taxCode") String taxCode,
            @RequestParam("listHotelService") List<Long> listHotelService,
            @RequestParam("wardId") Long wardId,
            @RequestParam("address1") String address1,
            @RequestParam("address2") String address2,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("fileImage") List<MultipartFile> fileImage
    ) throws Exception {
        CreateHotelRequest hotelRequest = new CreateHotelRequest();
        hotelRequest.setName(name);
        hotelRequest.setStar(star);
        hotelRequest.setDescription(description);
        hotelRequest.setNote(note);
        hotelRequest.setBusinessOwner(businessOwner);
        hotelRequest.setTaxCode(taxCode);
        hotelRequest.setListHotelService(listHotelService);
        hotelRequest.setWardId(wardId);
        hotelRequest.setAddress1(address1);
        hotelRequest.setAddress2(address2);
        hotelRequest.setPhoneNumber(phoneNumber);
        return hotelService.createHotel(hotelRequest, fileImage);
    }


    @PostMapping("/search-hotel")
    public ResponseListWithMetaData<SearchHotelResponse> searchHotel(@RequestBody @Null SearchHotelRequest searchHotelRequest) throws Exception {
        CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return hotelService.searchHotel(searchHotelRequest, principal);
    }

    @GetMapping("/get-hotel/{id}")
    public ResponseData<GetHotelResponseVerTwo> getHotelById(@PathVariable("id") Long id) throws Exception {
        return hotelService.getHotelById(id);
    }

    @PostMapping("/update-status")
    public ResultResponse updateStatus(@RequestBody UpdateStatusRequest request) throws SQLException {
        return hotelService.updateStatus(request);
    }

    @PostMapping("/update-hotel")
    public ResultResponse updateHotel(
            @RequestParam("id") Long id,
            @RequestParam("name") String name,
            @RequestParam("star") Integer star,
            @RequestParam("description") String description,
            @RequestParam("note") String note,
            @RequestParam("businessOwner") Long businessOwner,
            @RequestParam("taxCode") String taxCode,
            @RequestParam("listHotelService") List<Long> listHotelService,
            @RequestParam("wardId") Long wardId,
            @RequestParam("address1") String address1,
            @RequestParam("address2") String address2,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("fileImage1") Object image1,
            @RequestParam("fileImage2") Object image2,
            @RequestParam("fileImage3") Object image3) throws Exception {
        UpdateHotelRequest request = UpdateHotelRequest.builder()
                .id(id)
                .name(name)
                .star(star)
                .description(description)
                .note(note)
                .businessOwner(businessOwner)
                .taxCode(taxCode)
                .listHotelService(listHotelService)
                .wardId(wardId)
                .address1(address1)
                .address2(address2)
                .phoneNumber(phoneNumber)
                .build();
        return hotelService.updateHotel(request, image1, image2, image3);

    }

}
