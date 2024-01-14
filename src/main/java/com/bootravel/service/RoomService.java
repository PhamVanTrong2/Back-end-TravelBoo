package com.bootravel.service;



import com.bootravel.common.constant.FileAwsConstants;
import com.bootravel.common.constant.MessageConstants;
import com.bootravel.common.dto.BaseSearchPagingDTO;
import com.bootravel.common.dto.PageMetaDTO;
import com.bootravel.common.security.jwt.dto.CustomUserDetails;
import com.bootravel.entity.*;

import com.bootravel.exception.BadRequestAlertException;
import com.bootravel.exception.CommonException;
import com.bootravel.payload.requests.*;
import com.bootravel.payload.responses.*;
import com.bootravel.payload.responses.data.ResponseData;
import com.bootravel.payload.responses.data.ResponseListData;
import com.bootravel.payload.responses.data.ResponseListWithMetaData;
import com.bootravel.payload.responses.data.ResultResponse;
import com.bootravel.repository.CustomCommonRepository;
import com.bootravel.repository.RoomsRepository;
import com.bootravel.service.common.AmazonS3StorageHandler;
import com.bootravel.service.common.CommonService;
import com.bootravel.utils.I18n;
import lombok.var;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class RoomService {

    @Autowired
    private RoomsRepository roomsRepository;

    private static final String ENTITY_NAME = "RoomService";

    @Autowired
    private CommonService commonService;

    @Autowired
    private CustomCommonRepository customCommonRepository;

    @Autowired
    private PromotionService promotionService;

    @Autowired
    private AmazonS3StorageHandler amazonS3StorageHandler;

    private static final List<String> HEADER_SORT = Arrays.asList("userName");

    private static final String DEFAULT_SORT = "id";

    public ResultResponse createRoom(CreateRoomRequest request, List<MultipartFile> multipartFile) throws Exception {
        ResultResponse response = new ResultResponse();

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (roomsRepository.checkRoomCode(null, request.getRoomCode(), false, userDetails.getId())) {
            throw new CommonException(I18n.get(MessageConstants.INFO_ALREADY_EXISTS, "Room Code"));
        }

        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new BadRequestAlertException("No files uploaded", RoomsEntity.class.toString(), "NO_FILES_UPLOADED");
        }

        Long roomId = roomsRepository.getSeqRoom();

        roomsRepository.createRooms(request, roomId, userDetails.getId());

        roomsRepository.insertBedInRoom(request.getBedsRoom(), roomId);

        roomsRepository.insertRoomService(roomId, request.getListService());

        // xu li up anh
        List<String> allowedExtensions = Arrays.asList(FileAwsConstants.PNG, FileAwsConstants.JPEG, FileAwsConstants.JPG);
        List<String> fileUrls = new ArrayList<>();
        for (MultipartFile multipartFiles : multipartFile) {
            var bytes = multipartFiles.getBytes();
            InputStream inputStream = new ByteArrayInputStream(bytes);
            var fileExtension = FilenameUtils.getExtension(Objects.requireNonNull(multipartFiles.getOriginalFilename()));
            if (!allowedExtensions.contains(fileExtension))
                throw new BadRequestAlertException("file invalid", HotelsEntity.class.toString(), "FILE_INVALID");
            String contentType = FileAwsConstants.CONTENT_TYPE_JPEG;
            if (fileExtension.equals(FileAwsConstants.PNG))
                contentType = FileAwsConstants.CONTENT_TYPE_PNG;
            String filename = FileAwsConstants.FILE_ROOM + System.currentTimeMillis() + "_image." + fileExtension;
            var linkImage = amazonS3StorageHandler.storeFilePublic(inputStream, filename, contentType);
            fileUrls.add(linkImage);
        }

        roomsRepository.insertRoomImage(roomId, fileUrls);

        return response;
    }

    public ResultResponse setPriceByDate(SetPriceByDateRequest request) throws Exception {
        ResultResponse response = new ResultResponse();

        if(request.getRoomId() == null
            || request.getPrice() == null
                || request.getDateFrom() == null
                || request.getDateTo() == null
        ) {
            response.setCode(400);
            response.setMessage("Field is not null");
            return response;
        }

        if (request.getDateTo().before(request.getDateFrom())) {
            response.setCode(400);
            response.setMessage("DateTo must be greater then DateFrom");
            return response;
        }

        if (request.getPrice().compareTo(0L) <= 0) {
            response.setCode(400);
            response.setMessage("Price must be greater than 0");
            return response;
        }

        RoomsEntity room = roomsRepository.getRoomById(request.getRoomId());
        if(room == null) {
            response.setCode(400);
            response.setMessage("Room is not exist");
            return response;
        }
        if(new BigDecimal(request.getPrice()).compareTo(room.getMinPrice()) == -1) {
            response.setMessage("Price update must be less than min price");
            response.setCode(409);
            return response;
        }
        roomsRepository.setPriceByDate(request);
        response.setMessage("Update successfully");
        return response;
    }

    public ResultResponse setRoomAvailableByDate(SetRoomAvailableByDateRequest request) throws Exception {
        ResultResponse response = new ResultResponse();

        if (roomsRepository.checkMaxRoomAvailable(request)) {
            throw new CommonException(I18n.get(MessageConstants.EXCEEDS_THE_NUMBER_OF_AVAILABLE_ROOMS));
        }
        roomsRepository.setRoomAvailableByDate(request);

        return response;
    }


    public ResponseData<CalenderRoomAvailableResponse> searchRoomAvailableCalender(CalenderRoomRequest request) throws Exception {
        ResponseData<CalenderRoomAvailableResponse> responseListData = new ResponseData<>();
        CalenderRoomAvailableResponse calenderRoomAvailableResponse = new CalenderRoomAvailableResponse();
        List<RoomAvailableByDatesEntity> listRooms = roomsRepository.searchRoomsAvailableCalender(request);
        RoomsEntity room = roomsRepository.getRoomById(request.getRoomId());
        calenderRoomAvailableResponse.setNumberRoom(room.getRoomCount().longValue());
        calenderRoomAvailableResponse.setRoomAvailableByDatesEntity(listRooms);
        responseListData.setData(calenderRoomAvailableResponse);
        return responseListData;
    }

    public ResponseData<CalenderRoomPriceResponse> searchRoomPriceCalender(CalenderRoomRequest request) throws Exception {
        ResponseData<CalenderRoomPriceResponse> responseData = new ResponseData<>();
        CalenderRoomPriceResponse calenderRoomPriceResponse = new CalenderRoomPriceResponse();
        calenderRoomPriceResponse.setDefaultPrice(roomsRepository.getPriceDefault(request.getRoomId()));
        Map<Date, BigDecimal> mapPriceByDate = roomsRepository.searchRoomPriceCalender(request);
        calenderRoomPriceResponse.setMapPriceByDate(mapPriceByDate);
        responseData.setData(calenderRoomPriceResponse);
        return responseData;
    }

    public ResponseListWithMetaData<RoomResponse> searchRoomManagement(SearchRoomRequest request) throws Exception {
        ResponseListWithMetaData<RoomResponse> responseListData = new ResponseListWithMetaData<>();

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (Objects.isNull(request)) {
            request = new SearchRoomRequest();
        }

        Integer totalRecords = roomsRepository.getTotalRoom(userDetails.getId(), request);

        BaseSearchPagingDTO pagingDTO = request.getBaseSearchPagingDTO();
        PageMetaDTO meta = commonService.settingPageMetaInfo(request.getBaseSearchPagingDTO(),
                StringUtils.isEmpty(pagingDTO.getSortBy()) ? HEADER_SORT : Collections.singletonList(pagingDTO.getSortBy()),
                DEFAULT_SORT, totalRecords);

        List<RoomResponse> searchRoomsList = roomsRepository.searchRoomManagements(userDetails.getId(), request);

        responseListData.setMeta(meta);
        responseListData.setData(searchRoomsList);
        return responseListData;
    }

    public ResponseListData<BedType> getAllBedType() throws Exception {
        ResponseListData<BedType> responseListData = new ResponseListData<>();
        responseListData.setListData(customCommonRepository.getBedType());
        return responseListData;
    }

    public ResponseListData<RoomTypesEntity> getAllRoomType() throws Exception {
        ResponseListData<RoomTypesEntity> responseListData = new ResponseListData<>();
        responseListData.setListData(customCommonRepository.getAllRoomType());
        return responseListData;
    }

    public RoomResponeseVerTwo getRoomDetails(RoomRequestVerTwo roomRequestVerTwo) throws Exception {
        if (roomRequestVerTwo.getNumberRoom() == null && roomRequestVerTwo.getDateTo() == null &&
                roomRequestVerTwo.getDateFrom() == null && roomRequestVerTwo.getNumberPeople() == null) {
            var room = roomsRepository.getRoomById(roomRequestVerTwo.getId());
            Map<Long, Price> mapPriceCalender = new HashMap<>();
            if (Objects.nonNull(room)) {
                mapPriceCalender = roomsRepository.searchListRoomPriceCalender(roomRequestVerTwo.getDateFrom(),
                        roomRequestVerTwo.getDateTo(), new ArrayList<>(Arrays.asList(room)));

            }
            var roomType = roomsRepository.getRoomTypeById(room.getRoomTypeId());
            RoomResponeseVerTwo roomResponeseVerTwo = new RoomResponeseVerTwo();
            roomResponeseVerTwo.setId(room.getId());
            roomResponeseVerTwo.setName(room.getName());
            roomResponeseVerTwo.setRoomCount(room.getRoomCount());
            roomResponeseVerTwo.setRoomSize(room.getRoomSize());
            roomResponeseVerTwo.setRoomTypeName(roomType.getName());

            roomResponeseVerTwo.setDefaultPrice(room.getDefaultPrice());
            roomResponeseVerTwo.setMinPrice(room.getMinPrice());
            roomResponeseVerTwo.setWeekPrice(room.getWeekPrice());
            roomResponeseVerTwo.setMonthPrice(room.getMonthPrice());
            roomResponeseVerTwo.setAdditionalAdultFee(room.getAdditionalAdultFee());
            roomResponeseVerTwo.setAdditionalChildFee(room.getAdditionalChildFee());
            roomResponeseVerTwo.setMaxPeopleStay(room.getMaxPeopleStay());
            roomResponeseVerTwo.setStatus(room.getStatus());
            roomResponeseVerTwo.setHotelId(room.getHotelId());
            roomResponeseVerTwo.setPriceIncludesBreakfast(room.getPriceIncludesBreakfast());
            roomResponeseVerTwo.setComfirmationWithinMinute(room.getComfirmationWithinMinute());
            roomResponeseVerTwo.setSurchargeForAdultChild(room.getSurchargeForAdultChild());
            roomResponeseVerTwo.setStandardNumberOfPeople(room.getStandardNumberOfPeople());
            roomResponeseVerTwo.setComfirmNow(room.getComfirmNow());
            List<BedRoomInfo> bedsRooms = roomsRepository.getBedRoomInfoByRoomId(room.getId());
            roomResponeseVerTwo.setBedsRooms(bedsRooms);
            List<String> roomImages = roomsRepository.getImage(room.getId());
            roomResponeseVerTwo.setListImage(roomImages);
            Date dateNew1 = Date.from(Instant.now(Clock.system(ZoneId.systemDefault())));
            LocalDate localDateNew1 = Instant.now().atZone(ZoneId.systemDefault()).toLocalDate();
            localDateNew1 = localDateNew1.plusDays(5);

            CalenderRoomRequest calenderRoomRequest = new CalenderRoomRequest();
            calenderRoomRequest.setDateTo(Date.from(localDateNew1.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            calenderRoomRequest.setRoomId(roomRequestVerTwo.getId());
            calenderRoomRequest.setDateFrom(dateNew1);


            roomResponeseVerTwo.setPriceByDateString(calculatePrice(roomRequestVerTwo.getDateFrom(),
                    roomRequestVerTwo.getDateTo(),mapPriceCalender.get(room.getId()).getMonthPrice(),
                    mapPriceCalender.get(room.getId()).getWeekPrice(),mapPriceCalender.get(room.getId()).getMapPriceByDate()));
            roomResponeseVerTwo.setRoomCode(room.getRoomCode());

            var promotion = promotionService.getPromotionById(40L);
            PromotionDefaultResponse promotionDefaultResponse = new PromotionDefaultResponse();
            promotionDefaultResponse.setDiscountPercent(promotion.getData().getDiscountPercent());
            promotionDefaultResponse.setTypePromotion(promotion.getData().getTypePromotion());
            promotionDefaultResponse.setPromotionCode(promotion.getData().getCode());
            promotionDefaultResponse.setMaxDiscount(promotion.getData().getMaxDiscount());
            roomResponeseVerTwo.setListService(room.getListService());
            roomResponeseVerTwo.setPromotion(promotionDefaultResponse);
            roomResponeseVerTwo.setRoomCode(room.getRoomCode());

            return roomResponeseVerTwo;
        }else {
            var result = roomsRepository.checkRoomAvailableByRoomId(roomRequestVerTwo);
            if(result != null){
                var room = roomsRepository.getRoomById(roomRequestVerTwo.getId());
                Map<Long, Price> mapPriceCalender = new HashMap<>();
                if (Objects.nonNull(room)) {
                    mapPriceCalender = roomsRepository.searchListRoomPriceCalender(roomRequestVerTwo.getDateFrom(),
                            roomRequestVerTwo.getDateTo(), new ArrayList<>(Arrays.asList(room)));

                }
                var roomType = roomsRepository.getRoomTypeById(room.getRoomTypeId());
                RoomResponeseVerTwo roomResponeseVerTwo = new RoomResponeseVerTwo();
                roomResponeseVerTwo.setId(room.getId());
                roomResponeseVerTwo.setName(room.getName());
                roomResponeseVerTwo.setRoomCount(room.getRoomCount());
                roomResponeseVerTwo.setRoomSize(room.getRoomSize());
                roomResponeseVerTwo.setRoomTypeName(roomType.getName());

                roomResponeseVerTwo.setDefaultPrice(room.getDefaultPrice());
                roomResponeseVerTwo.setMinPrice(room.getMinPrice());
                roomResponeseVerTwo.setWeekPrice(room.getWeekPrice());
                roomResponeseVerTwo.setMonthPrice(room.getMonthPrice());
                roomResponeseVerTwo.setAdditionalAdultFee(room.getAdditionalAdultFee());
                roomResponeseVerTwo.setAdditionalChildFee(room.getAdditionalChildFee());
                roomResponeseVerTwo.setMaxPeopleStay(room.getMaxPeopleStay());
                roomResponeseVerTwo.setStatus(room.getStatus());
                roomResponeseVerTwo.setHotelId(room.getHotelId());
                roomResponeseVerTwo.setPriceIncludesBreakfast(room.getPriceIncludesBreakfast());
                roomResponeseVerTwo.setComfirmationWithinMinute(room.getComfirmationWithinMinute());
                roomResponeseVerTwo.setSurchargeForAdultChild(room.getSurchargeForAdultChild());
                roomResponeseVerTwo.setStandardNumberOfPeople(room.getStandardNumberOfPeople());
                roomResponeseVerTwo.setComfirmNow(room.getComfirmNow());
                List<BedRoomInfo> bedsRooms = roomsRepository.getBedRoomInfoByRoomId(room.getId());
                roomResponeseVerTwo.setBedsRooms(bedsRooms);
                List<String> roomImages = roomsRepository.getImage(room.getId());
                roomResponeseVerTwo.setListImage(roomImages);
                roomResponeseVerTwo.setListService(room.getListService());

                roomResponeseVerTwo.setPriceByDateString(calculatePrice(roomRequestVerTwo.getDateFrom(),
                        roomRequestVerTwo.getDateTo(),mapPriceCalender.get(room.getId()).getMonthPrice(),
                        mapPriceCalender.get(room.getId()).getWeekPrice(),mapPriceCalender.get(room.getId()).getMapPriceByDate()));

                var promotion = promotionService.getPromotionById(40L);
                PromotionDefaultResponse promotionDefaultResponse = new PromotionDefaultResponse();
                promotionDefaultResponse.setDiscountPercent(promotion.getData().getDiscountPercent());
                promotionDefaultResponse.setTypePromotion(promotion.getData().getTypePromotion());
                promotionDefaultResponse.setPromotionCode(promotion.getData().getCode());
                promotionDefaultResponse.setMaxDiscount(promotion.getData().getMaxDiscount());
                roomResponeseVerTwo.setPromotion(promotionDefaultResponse);
                roomResponeseVerTwo.setRoomCode(room.getRoomCode());
                return roomResponeseVerTwo;
            }else{
                throw new BadRequestAlertException("Not found room", ENTITY_NAME, "404");
            }
        }
    }
    public RoomResponeseVerTwo getRoomDetailsHotel(RoomRequestVerTwo roomRequestVerTwo) throws Exception {
        if (roomRequestVerTwo.getNumberRoom() == null && roomRequestVerTwo.getDateTo() == null &&
                roomRequestVerTwo.getDateFrom() == null && roomRequestVerTwo.getNumberPeople() == null) {
            var room = roomsRepository.getRoomById(roomRequestVerTwo.getId());

            var roomType = roomsRepository.getRoomTypeById(room.getRoomTypeId());
            RoomResponeseVerTwo roomResponeseVerTwo = new RoomResponeseVerTwo();
            roomResponeseVerTwo.setId(room.getId());
            roomResponeseVerTwo.setName(room.getName());
            roomResponeseVerTwo.setRoomCount(room.getRoomCount());
            roomResponeseVerTwo.setRoomSize(room.getRoomSize());
            roomResponeseVerTwo.setRoomTypeName(roomType.getName());

            Date dateNew = Date.from(Instant.now(Clock.system(ZoneId.systemDefault())));
            var price = roomsRepository.getPriceByDateRoomVerTwo(room.getId(), dateNew);
            if(price != null){
                roomResponeseVerTwo.setPriceBydate(price);
            }else{
                roomResponeseVerTwo.setPriceBydate(null);
            }
            roomResponeseVerTwo.setDefaultPrice(room.getDefaultPrice());
            roomResponeseVerTwo.setMinPrice(room.getMinPrice());
            roomResponeseVerTwo.setWeekPrice(room.getWeekPrice());
            roomResponeseVerTwo.setMonthPrice(room.getMonthPrice());
            roomResponeseVerTwo.setAdditionalAdultFee(room.getAdditionalAdultFee());
            roomResponeseVerTwo.setAdditionalChildFee(room.getAdditionalChildFee());
            roomResponeseVerTwo.setMaxPeopleStay(room.getMaxPeopleStay());
            roomResponeseVerTwo.setStatus(room.getStatus());
            roomResponeseVerTwo.setHotelId(room.getHotelId());
            roomResponeseVerTwo.setPriceIncludesBreakfast(room.getPriceIncludesBreakfast());
            roomResponeseVerTwo.setComfirmationWithinMinute(room.getComfirmationWithinMinute());
            roomResponeseVerTwo.setSurchargeForAdultChild(room.getSurchargeForAdultChild());
            roomResponeseVerTwo.setComfirmNow(room.getComfirmNow());
            List<BedRoomInfo> bedsRooms = roomsRepository.getBedRoomInfoByRoomId(room.getId());
            roomResponeseVerTwo.setBedsRooms(bedsRooms);
            List<String> roomImages = roomsRepository.getImage(room.getId());
            roomResponeseVerTwo.setListImage(roomImages);
            roomResponeseVerTwo.setListService(room.getListService());
            roomResponeseVerTwo.setStandardNumberOfPeople(room.getStandardNumberOfPeople());
//            if(roomRequestVerTwo.getDateFrom() != null && roomRequestVerTwo.getDateTo() != null){
//                Date dateNew1 = Date.from(Instant.now(Clock.system(ZoneId.systemDefault())));
//                CalenderRoomRequest calenderRoomRequest = new CalenderRoomRequest();
//                calenderRoomRequest.setDateTo(roomRequestVerTwo.getDateTo());
//                calenderRoomRequest.setRoomId(roomRequestVerTwo.getId());
//
//                calenderRoomRequest.setDateFrom(dateNew1);
//                Map<Date, BigDecimal> mapPriceByDate = roomsRepository.searchRoomPriceCalender(calenderRoomRequest);
//                roomResponeseVerTwo.setPriceByPerDate(mapPriceByDate);
//            }
            Date dateNew1 = Date.from(Instant.now(Clock.system(ZoneId.systemDefault())));
            LocalDate localDateNew1 = Instant.now().atZone(ZoneId.systemDefault()).toLocalDate();
            localDateNew1 = localDateNew1.plusDays(5);

            CalenderRoomRequest calenderRoomRequest = new CalenderRoomRequest();
            calenderRoomRequest.setDateTo(Date.from(localDateNew1.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            calenderRoomRequest.setRoomId(roomRequestVerTwo.getId());
            calenderRoomRequest.setDateFrom(dateNew1);
            Map<Date, BigDecimal> mapPriceByDate = roomsRepository.searchRoomPriceCalender(calenderRoomRequest);

            var promotion = promotionService.getPromotionById(40L);
            PromotionDefaultResponse promotionDefaultResponse = new PromotionDefaultResponse();
            promotionDefaultResponse.setDiscountPercent(promotion.getData().getDiscountPercent());
            promotionDefaultResponse.setTypePromotion(promotion.getData().getTypePromotion());
            promotionDefaultResponse.setPromotionCode(promotion.getData().getCode());
            promotionDefaultResponse.setMaxDiscount(promotion.getData().getMaxDiscount());
            roomResponeseVerTwo.setPromotion(promotionDefaultResponse);
            roomResponeseVerTwo.setRoomCode(room.getRoomCode());
            return roomResponeseVerTwo;
        }else {
            var result = roomsRepository.checkRoomAvailableByRoomId(roomRequestVerTwo);
            if(result != null){
                var room = roomsRepository.getRoomById(roomRequestVerTwo.getId());
                var roomType = roomsRepository.getRoomTypeById(room.getRoomTypeId());
                var checkNumberRoom = roomsRepository.getRoomAvailableByRoomIdAndDateApply(room.getId(),roomRequestVerTwo.getDateFrom());
                if(checkNumberRoom.getNumberRoomAvailable() > 0 && room.getMaxPeopleStay() > 0){
                    RoomResponeseVerTwo roomResponeseVerTwo = new RoomResponeseVerTwo();
                    roomResponeseVerTwo.setId(room.getId());
                    roomResponeseVerTwo.setName(room.getName());
                    roomResponeseVerTwo.setRoomCount(room.getRoomCount());
                    roomResponeseVerTwo.setRoomSize(room.getRoomSize());
                    roomResponeseVerTwo.setRoomTypeName(roomType.getName());

                    LocalDate checkinDate = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));
                    Instant instant = checkinDate.atStartOfDay(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant();
                    Date dateApply = Date.from(instant);
                    var price = roomsRepository.getPriceByDateRoomVerTwo(room.getId(), dateApply);
                    if(price != null){
                        roomResponeseVerTwo.setPriceBydate(price);
                    }else{
                        roomResponeseVerTwo.setPriceBydate(null);
                    }
                    roomResponeseVerTwo.setDefaultPrice(room.getDefaultPrice());
                    roomResponeseVerTwo.setMinPrice(room.getMinPrice());
                    roomResponeseVerTwo.setWeekPrice(room.getWeekPrice());
                    roomResponeseVerTwo.setMonthPrice(room.getMonthPrice());
                    roomResponeseVerTwo.setAdditionalAdultFee(room.getAdditionalAdultFee());
                    roomResponeseVerTwo.setAdditionalChildFee(room.getAdditionalChildFee());
                    roomResponeseVerTwo.setMaxPeopleStay(room.getMaxPeopleStay());
                    roomResponeseVerTwo.setStatus(room.getStatus());
                    roomResponeseVerTwo.setHotelId(room.getHotelId());
                    roomResponeseVerTwo.setPriceIncludesBreakfast(room.getPriceIncludesBreakfast());
                    roomResponeseVerTwo.setComfirmationWithinMinute(room.getComfirmationWithinMinute());
                    roomResponeseVerTwo.setSurchargeForAdultChild(room.getSurchargeForAdultChild());
                    roomResponeseVerTwo.setComfirmNow(room.getComfirmNow());
                    List<BedRoomInfo> bedsRooms = roomsRepository.getBedRoomInfoByRoomId(room.getId());
                    roomResponeseVerTwo.setBedsRooms(bedsRooms);
                    List<String> roomImages = roomsRepository.getImage(room.getId());
                    roomResponeseVerTwo.setListImage(roomImages);
                    roomResponeseVerTwo.setListService(room.getListService());
                    if(roomRequestVerTwo.getDateFrom() != null && roomRequestVerTwo.getDateTo() != null){

                        CalenderRoomRequest calenderRoomRequest = new CalenderRoomRequest();
                        calenderRoomRequest.setDateTo(roomRequestVerTwo.getDateTo());
                        calenderRoomRequest.setRoomId(roomRequestVerTwo.getId());
                        calenderRoomRequest.setDateFrom(roomRequestVerTwo.getDateFrom());
                        Map<Date, BigDecimal> mapPriceByDate = roomsRepository.searchRoomPriceCalender(calenderRoomRequest);

                    }
                    roomResponeseVerTwo.setStandardNumberOfPeople(room.getStandardNumberOfPeople());
                    var promotion = promotionService.getPromotionById(40L);
                    PromotionDefaultResponse promotionDefaultResponse = new PromotionDefaultResponse();
                    promotionDefaultResponse.setDiscountPercent(promotion.getData().getDiscountPercent());
                    promotionDefaultResponse.setTypePromotion(promotion.getData().getTypePromotion());
                    promotionDefaultResponse.setPromotionCode(promotion.getData().getCode());
                    promotionDefaultResponse.setMaxDiscount(promotion.getData().getMaxDiscount());
                    roomResponeseVerTwo.setPromotion(promotionDefaultResponse);
                    roomResponeseVerTwo.setRoomCode(room.getRoomCode());
                    return roomResponeseVerTwo;
                }else{
                    System.out.println("No room details available.");
                }

                }else {
                    System.out.println("No room details available.");
                }

        }


        return null;
    }

    public List<RoomResponeseVerTwo> getRoomByHotelId(RoomRequestVerTwo request) {
        List<RoomResponeseVerTwo> roomList = new ArrayList<>();
        Map<Long, Price> mapPriceCalender = new HashMap<>();
        try {
            List<RoomsEntity> rooms = roomsRepository.getRoomByHotelIdGuest(request);
            if (!rooms.isEmpty()) {
                    mapPriceCalender = roomsRepository.searchListRoomPriceCalender(request.getDateFrom(),
                            request.getDateTo(), rooms);
            }
            for (RoomsEntity room : rooms) {
                RoomRequestVerTwo roomRequest = new RoomRequestVerTwo();
                roomRequest.setId(room.getId());
                roomRequest.setDateFrom(request.getDateFrom());
                roomRequest.setDateTo(request.getDateTo());
                roomRequest.setNumberRoom(request.getNumberRoom());
                roomRequest.setNumberPeople(request.getNumberPeople());
                RoomResponeseVerTwo roomResponse = getRoomDetailsHotel(roomRequest);
                if (roomResponse != null) {
                    roomResponse.setPriceByDateString(calculatePrice(request.getDateFrom(),
                            request.getDateTo(),mapPriceCalender.get(room.getId()).getMonthPrice(),
                            mapPriceCalender.get(room.getId()).getWeekPrice(),mapPriceCalender.get(room.getId()).getMapPriceByDate()));

                    roomList.add(roomResponse);
                }
                continue;
            }
        } catch (Exception e) {
            // Handle exceptions as needed
            throw new BadRequestAlertException("Hotel id invalid", ENTITY_NAME, "hotelId_Invalid");
        }
        return roomList;
    }


    public List<RoomResponeseVerTwo> searchRoomUser(CheckRoomByDateRequest filterParams) throws Exception {
        List<RoomResponeseVerTwo> roomList = new ArrayList<>();
        try {
            List<RoomsEntity> rooms = roomsRepository.filterRoom(filterParams);

            for (RoomsEntity room : rooms) {
                RoomRequestVerTwo roomRequest = new RoomRequestVerTwo();
                roomRequest.setId(room.getId());

                RoomResponeseVerTwo roomResponse = getRoomDetails(roomRequest);
                roomList.add(roomResponse);
            }
        } catch (Exception e) {
            // Handle exceptions as needed
            e.printStackTrace(); // Replace this with proper exception handling
        }
        return roomList;
    }


    public List<BedRoomInfo> getRoomTypeByRoomId(long id ){
        return roomsRepository.getBedRoomInfoByRoomId(id);
    }

    public List<String> getService(long id ){
        return roomsRepository.getAllServiceByRoomId(id);
    }

    public ResultResponse updateRoom(UpdateRoomRequest request, Object image1, Object image2, Object image3) throws Exception {
        ResultResponse response = new ResultResponse();
        if (roomsRepository.checkRoomCode(request.getRoomId(), request.getRoomCode(), true, null)) {
            throw new CommonException(I18n.get(MessageConstants.INFO_ALREADY_EXISTS, "Room Code"));
        }

        List<String> imageNoChange = new ArrayList<>();
        List<MultipartFile> imageChange = new ArrayList<>();
        if(image1 instanceof String) {
            imageNoChange.add((String) image1);
        } else if (image1 instanceof MultipartFile) {
            imageChange.add((MultipartFile) image1);
        }

        if(image2 instanceof String) {
            imageNoChange.add((String) image2);
        } else if (image2 instanceof MultipartFile) {
            imageChange.add((MultipartFile) image2);
        }

        if(image3 instanceof String) {
            imageNoChange.add((String) image3);
        } else if (image3 instanceof MultipartFile) {
            imageChange.add((MultipartFile) image3);
        }

        List<String> listImageOldDelete = roomsRepository.getListImageOldDelete(request.getRoomId(), imageNoChange);

        roomsRepository.deleteListImage(request.getRoomId(), imageNoChange);
        
        for (String url: listImageOldDelete) {
            amazonS3StorageHandler.deleteFile(url);
        }
        // B2: insert anh moi vao
        List<String> allowedExtensions = Arrays.asList(FileAwsConstants.PNG, FileAwsConstants.JPEG, FileAwsConstants.JPG);
        List<String> fileUrls = new ArrayList<>();
        for (MultipartFile multipartFiles : imageChange) {
            var bytes = multipartFiles.getBytes();
            InputStream inputStream = new ByteArrayInputStream(bytes);
            var fileExtension = FilenameUtils.getExtension(Objects.requireNonNull(multipartFiles.getOriginalFilename()));
            if (!allowedExtensions.contains(fileExtension))
                throw new BadRequestAlertException("file invalid", HotelsEntity.class.toString(), "FILE_INVALID");
            String contentType = FileAwsConstants.CONTENT_TYPE_JPEG;
            if (fileExtension.equals(FileAwsConstants.PNG))
                contentType = FileAwsConstants.CONTENT_TYPE_PNG;
            String filename = FileAwsConstants.FILE_ROOM + System.currentTimeMillis() + "_image." + fileExtension;
            var linkImage = amazonS3StorageHandler.storeFilePublic(inputStream, filename, contentType);
            fileUrls.add(linkImage);
        }
        roomsRepository.insertRoomImage(request.getRoomId(), fileUrls);

        roomsRepository.updateRooms(request);

        roomsRepository.deleteBedRoom(request.getRoomId());

        roomsRepository.insertBedInRoom(request.getBedsRoom(), request.getRoomId());

        roomsRepository.deleteRoomService(request.getRoomId());

        roomsRepository.insertRoomService(request.getRoomId(), request.getListService());


        return response;
    }

    public ResponseData<GetRoomByIdResponse> getRoomById(long id) {
        ResponseData<GetRoomByIdResponse> responseData = new ResponseData<>();
        RoomResponeseVerTwo roomResponeseVerTwo = roomsRepository.getRoomDetailById(id);
        List<BedRoomInfo> bedsRooms = roomsRepository.getBedRoomInfoByRoomId(id);
        roomResponeseVerTwo.setBedsRooms(bedsRooms);
        List<String> roomImages = roomsRepository.getImage(id);
        roomResponeseVerTwo.setListImage(roomImages);
        GetRoomByIdResponse getRoomByIdResponse = new GetRoomByIdResponse();
        getRoomByIdResponse.setListRoomService(roomsRepository.getListServiceByRoomId(id));
        getRoomByIdResponse.setResponeseVerTwo(roomResponeseVerTwo);
        responseData.setData(getRoomByIdResponse);
        return responseData;
    }

    public ResponseListWithMetaData<RoomResponse> searchRoomManagementBookingStaff(SearchRoomRequest request) throws Exception {
        ResponseListWithMetaData<RoomResponse> responseListData = new ResponseListWithMetaData<>();

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (Objects.isNull(request)) {
            request = new SearchRoomRequest();
        }

        Integer totalRecords = roomsRepository.getTotalRoomBookingStaff(userDetails.getId(), request);

        BaseSearchPagingDTO pagingDTO = request.getBaseSearchPagingDTO();
        PageMetaDTO meta = commonService.settingPageMetaInfo(request.getBaseSearchPagingDTO(),
                StringUtils.isEmpty(pagingDTO.getSortBy()) ? HEADER_SORT : Collections.singletonList(pagingDTO.getSortBy()),
                DEFAULT_SORT, totalRecords);

        List<RoomResponse> searchRoomsList = roomsRepository.searchRoomManagementsBookingStaff(userDetails.getId(), request);

        responseListData.setMeta(meta);
        responseListData.setData(searchRoomsList);
        return responseListData;
    }

    public ResponseListWithMetaData<RoomResponse> searchListRoomManagement() throws Exception {
        ResponseListWithMetaData<RoomResponse> responseListData = new ResponseListWithMetaData<>();

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<RoomResponse> searchRoomsList = roomsRepository.searchListRoomManagement(userDetails.getId());

        responseListData.setData(searchRoomsList);

        return responseListData;
    }

    public ResponseData<GetRoomResponse> getRoom(Long id) {
        ResponseData<GetRoomResponse> responseData = new ResponseData<>();
        List<BedRoomInfo> bedRooms = roomsRepository.getBedRoomInfoByRoomId(id);
        List<String> roomServices = roomsRepository.getAllServiceByRoomId(id);
        List<String> roomImages = roomsRepository.getImage(id);
        RoomsEntity entity = roomsRepository.getRoomById(id);
        RoomTypesEntity roomTypeName = roomsRepository.getRoomTypeById(entity.getRoomTypeId());

        GetRoomResponse room = GetRoomResponse.builder()
                .roomId(id)
                .roomName(entity.getName())
                .roomCode(entity.getRoomCode())
                .roomCount(entity.getRoomCount())
                .roomSize(entity.getRoomSize())
                .roomTypeName(roomTypeName.getName())
                .maxPeopleStay(entity.getMaxPeopleStay())
                .standardNumberOfPeople(entity.getStandardNumberOfPeople().intValue())
                .confirmNow(entity.getComfirmNow())
                .confirmationWithinMinute(entity.getComfirmationWithinMinute())
                .priceIncludesBreakfast(entity.getPriceIncludesBreakfast())
                .surchargeForAdultChild(entity.getSurchargeForAdultChild())
                .minPrice(entity.getMinPrice())
                .defaultPrice(entity.getDefaultPrice())
                .weekPrice(entity.getWeekPrice())
                .monthPrice(entity.getMonthPrice())
                .additionalAdultFee(entity.getAdditionalAdultFee())
                .additionalChildFee(entity.getAdditionalChildFee())
                .bedsRooms(bedRooms)
                .listService(roomServices)
                .listImage(roomImages)
                .build();

        responseData.setData(room);
        return responseData;
    }

    public Map<String, BigDecimal> calculatePrice(Date startDate, Date endDate, BigDecimal monthlyPrice,
                                                    BigDecimal weeklyPrice, Map<String, BigDecimal> dailyPrice) {
        Map<String, BigDecimal> priceMap = new LinkedHashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        String startDateStr = sdf.format(startDate);
        String endDateStr = sdf.format(endDate);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        LocalDate start = LocalDate.parse(startDateStr, formatter);
        LocalDate end = LocalDate.parse(endDateStr, formatter);
        Period period = Period.between(start, end);
        int months = period.getMonths();
        int remainingDays = period.getDays();

        if (months >= 1) {
            for (int i = 0; i < months; i++) {
                String monthRange = start + "/" + start.plusMonths(1);
                priceMap.put(monthRange, monthlyPrice);
                start = start.plusMonths(1);
            }
        }

        if (remainingDays >= 7) {
            int weeks = remainingDays / 7;
            for (int i = 0; i < weeks; i++) {
                String weekRange = start + "/" + start.plusWeeks(1);
                priceMap.put(weekRange, weeklyPrice);
                start = start.plusWeeks(1);
            }
        }

        while (start.isBefore(end)) {
            String date = start+ "/" + start.plusDays(1);
            BigDecimal priceByDate = dailyPrice.getOrDefault(start.toString(), BigDecimal.valueOf(0));
            priceMap.put(date, priceByDate);
            start = start.plusDays(1);
        }


        return priceMap;
    }
}
