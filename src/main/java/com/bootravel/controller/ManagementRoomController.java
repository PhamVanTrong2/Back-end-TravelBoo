package com.bootravel.controller;

import com.bootravel.entity.BedType;
import com.bootravel.entity.RoomTypesEntity;
import com.bootravel.payload.requests.*;
import com.bootravel.payload.requests.commonRequests.RoomFilterRequest;
import com.bootravel.payload.responses.*;
import com.bootravel.payload.responses.constant.ResponseType;
import com.bootravel.payload.responses.data.ResponseData;
import com.bootravel.payload.responses.data.ResponseListData;
import com.bootravel.payload.responses.data.ResponseListWithMetaData;
import com.bootravel.payload.responses.data.ResultResponse;
import com.bootravel.service.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/room")
public class ManagementRoomController {

    @Autowired
    private RoomService roomService;

    @PostMapping(value = "/create-room")
    public ResultResponse createRoom(
            @RequestParam("roomName") String roomName,
            @RequestParam("roomCount") Integer roomCount,
            @RequestParam("roomSize") Integer roomSize,
            @RequestParam("roomTypeId") Integer roomTypeId,
            @RequestParam("minPrice") Integer minPrice,
            @RequestParam("defaultPrice") Integer defaultPrice,
            @RequestParam("weekPrice") Integer weekPrice,
            @RequestParam("monthPrice") Integer monthPrice,
            @RequestParam("additionalAdultFee") Integer additionalAdultFee,
            @RequestParam("additionalChildFee") Integer additionalChildFee,
            @RequestParam("maxPeopleStay") Integer maxPeopleStay,
            @RequestParam("surchargeForAdultChild") String surchargeForAdultChild,
            @RequestParam("priceIncludesBreakfast") String priceIncludesBreakfast,
            @RequestParam("confirmationWithinMinute") String confirmationWithinMinute,
            @RequestParam("confirmNow") String confirmNow,
            @RequestParam("listService") List<Long> listService,
            @RequestParam("arrayKeyBed") List<Long> arrayKeyBed,
            @RequestParam("arrayValueBed") List<Long> arrayValueBed,
            @RequestParam("roomCode") String roomCode,
            @RequestParam("standardNumberOfPeople") Long standardNumberOfPeople,
            @RequestParam("fileImage") List<MultipartFile> fileImage
    ) throws Exception {
        Map<Long, Long> bedsRoom = new HashMap<>();
        for (int i = 0; i < arrayKeyBed.size(); i++) {
            bedsRoom.put(arrayKeyBed.get(i), arrayValueBed.get(i));
        }
        CreateRoomRequest request = CreateRoomRequest.builder()
                .roomName(roomName)
                .roomCount(roomCount)
                .roomSize(roomSize)
                .roomTypeId(roomTypeId)
                .minPrice(new BigDecimal(minPrice))
                .defaultPrice(new BigDecimal(defaultPrice))
                .weekPrice(new BigDecimal(weekPrice))
                .monthPrice(new BigDecimal(monthPrice))
                .additionalAdultFee(new BigDecimal(additionalAdultFee))
                .additionalChildFee(new BigDecimal(additionalChildFee))
                .maxPeopleStay(maxPeopleStay)
                .surchargeForAdultChild(surchargeForAdultChild.equals("true"))
                .priceIncludesBreakfast(priceIncludesBreakfast.equals("true"))
                .confirmationWithinMinute(confirmationWithinMinute.equals("true"))
                .confirmNow(confirmNow.equals("true"))
                .listService(listService)
                .bedsRoom(bedsRoom)
                .roomCode(roomCode)
                .standardNumberOfPeople(standardNumberOfPeople)
                .build();
        return roomService.createRoom(request, fileImage);
    }

    @PostMapping("/set-price")
    public ResultResponse setPrice(@RequestBody SetPriceByDateRequest request) throws Exception {
        return roomService.setPriceByDate(request);
    }

    @PostMapping("/set-room-available")
    public ResultResponse setRoomAvailable(@RequestBody SetRoomAvailableByDateRequest request) throws Exception {
        return roomService.setRoomAvailableByDate(request);
    }


    @PostMapping("/search-room-available-calender")
    public ResponseData<CalenderRoomAvailableResponse> searchRoomAvailableCalender(@RequestBody CalenderRoomRequest request) throws Exception {
        return roomService.searchRoomAvailableCalender(request);
    }

    @PostMapping("/search-room-price-calender")
    public ResponseData<CalenderRoomPriceResponse> searchRoomPriceCalender(@RequestBody CalenderRoomRequest request) throws Exception {
        return roomService.searchRoomPriceCalender(request);
    }

    @PostMapping("search-room-management")
    public ResponseListWithMetaData<RoomResponse> searchRoomManagement(@RequestBody SearchRoomRequest request) throws Exception {
        return roomService.searchRoomManagement(request);
    }

    @PostMapping("get-all-bed-type")
    public ResponseListData<BedType> getBedType() throws Exception {
        return roomService.getAllBedType();
    }

    @PostMapping("get-all-room-type")
    public ResponseListData<RoomTypesEntity> getAllRoomType() throws Exception {
        return roomService.getAllRoomType();
    }

    @PostMapping("/get-room-by-id/{id}")
    public ResponseData<GetRoomByIdResponse> getRoomById(@PathVariable Long id) {
        return roomService.getRoomById(id);
    }

    @PostMapping("search-room-management-booking-staff")
    public ResponseListWithMetaData<RoomResponse> searchRoomManagementBookingStaff(@RequestBody SearchRoomRequest request) throws Exception {
        return roomService.searchRoomManagementBookingStaff(request);
    }

    @PostMapping("/search-list-room-management")
    public ResponseListWithMetaData<RoomResponse> searchListRoomManagement() throws Exception {
        return roomService.searchListRoomManagement();
    }

    @PostMapping("update-room")
    public ResultResponse updateRoom(
            @RequestParam("roomId") Long roomId,
            @RequestParam("roomName") String roomName,
            @RequestParam("roomCount") Integer roomCount,
            @RequestParam("roomSize") Integer roomSize,
            @RequestParam("roomTypeId") Integer roomTypeId,
            @RequestParam("minPrice") Integer minPrice,
            @RequestParam("defaultPrice") Integer defaultPrice,
            @RequestParam("weekPrice") Integer weekPrice,
            @RequestParam("monthPrice") Integer monthPrice,
            @RequestParam("additionalAdultFee") Integer additionalAdultFee,
            @RequestParam("additionalChildFee") Integer additionalChildFee,
            @RequestParam("maxPeopleStay") Integer maxPeopleStay,
            @RequestParam("surchargeForAdultChild") String surchargeForAdultChild,
            @RequestParam("priceIncludesBreakfast") String priceIncludesBreakfast,
            @RequestParam("confirmationWithinMinute") String confirmationWithinMinute,
            @RequestParam("confirmNow") String confirmNow,
            @RequestParam("listService") List<Long> listService,
            @RequestParam("arrayKeyBed") List<Long> arrayKeyBed,
            @RequestParam("arrayValueBed") List<Long> arrayValueBed,
            @RequestParam("roomCode") String roomCode,
            @RequestParam("standardNumberOfPeople") Long standardNumberOfPeople,
            @RequestParam("fileImage1") Object image1,
            @RequestParam("fileImage2") Object image2,
            @RequestParam("fileImage3") Object image3
    ) throws Exception {
        Map<Long, Long> bedsRoom = new HashMap<>();
        for (int i = 0; i < arrayKeyBed.size(); i++) {
            bedsRoom.put(arrayKeyBed.get(i), arrayValueBed.get(i));
        }
        UpdateRoomRequest request = UpdateRoomRequest.builder()
                .roomId(roomId)
                .roomName(roomName)
                .roomCount(roomCount)
                .roomSize(roomSize)
                .roomTypeId(roomTypeId)
                .minPrice(new BigDecimal(minPrice))
                .defaultPrice(new BigDecimal(defaultPrice))
                .weekPrice(new BigDecimal(weekPrice))
                .monthPrice(new BigDecimal(monthPrice))
                .additionalAdultFee(new BigDecimal(additionalAdultFee))
                .additionalChildFee(new BigDecimal(additionalChildFee))
                .maxPeopleStay(maxPeopleStay)
                .surchargeForAdultChild(surchargeForAdultChild.equals("true"))
                .priceIncludesBreakfast(priceIncludesBreakfast.equals("true"))
                .confirmationWithinMinute(confirmationWithinMinute.equals("true"))
                .confirmNow(confirmNow.equals("true"))
                .listService(listService)
                .bedsRoom(bedsRoom)
                .roomCode(roomCode)
                .standardNumberOfPeople(standardNumberOfPeople)
                .build();
        return roomService.updateRoom(request, image1, image2, image3);
    }

    @PostMapping("/get-room/{id}")
    public ResponseData<GetRoomResponse> getRoom(@PathVariable Long id) {
        return roomService.getRoom(id);
    }
}
