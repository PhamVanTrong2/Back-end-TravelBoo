package com.bootravel.service;


import com.bootravel.common.constant.FileAwsConstants;

import com.bootravel.common.dto.BaseSearchPagingDTO;
import com.bootravel.common.dto.PageMetaDTO;
import com.bootravel.common.security.jwt.dto.CustomUserDetails;
import com.bootravel.payload.requests.UpdateHotelRequest;
import com.bootravel.payload.responses.*;
import com.bootravel.payload.responses.data.ResponseListData;
import com.bootravel.entity.HotelsEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import com.bootravel.exception.BadRequestAlertException;
import com.bootravel.payload.requests.CreateHotelRequest;
import com.bootravel.payload.requests.SearchHotelRequest;
import com.bootravel.payload.requests.UpdateStatusRequest;
import com.bootravel.payload.requests.commonRequests.HotelFilterRequest;
import com.bootravel.payload.responses.data.ResponseData;
import com.bootravel.payload.responses.data.ResponseListWithMetaData;
import com.bootravel.payload.responses.data.ResultResponse;
import com.bootravel.repository.CustomCommonRepository;
import com.bootravel.repository.HotelRepository;
import com.bootravel.service.common.AmazonS3StorageHandler;
import com.bootravel.service.common.CommonService;
import lombok.var;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class HotelService {

    private static final String DEFAULT_SORT = "id";

    private static final String ENTITY_NAME = "HotelService";

    private static final List<String> HEADER_SORT = Arrays.asList("hotelName");


    @Autowired
    private CustomCommonRepository custormCommonRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private CommonService commonService;

    @Autowired
    private AmazonS3StorageHandler amazonS3StorageHandler;

    @Autowired
    private AddressService addressService;

    public ResultResponse createHotel(CreateHotelRequest request,List<MultipartFile> multipartFile) throws Exception {
        ResultResponse response = new ResultResponse();
        HotelsEntity hotelsEntity = parseRequestToHotelEntity(request);
        boolean isTaxCodeExist  = hotelRepository.checkTaxCode(request.getTaxCode());
        if(isTaxCodeExist) {
            throw new BadRequestAlertException("Tax code exist", HotelsEntity.class.toString(), "TAX_CODE_EXISTED");
        }
        boolean isPhoneExist = hotelRepository.checkPhoneNumber(request.getPhoneNumber());
        if(isPhoneExist) {
            throw new BadRequestAlertException("Phone number exist", HotelsEntity.class.toString(), "PHONE_NUMBER_EXIST");
        }
        //xu ly file
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new BadRequestAlertException("No files uploaded", HotelsEntity.class.toString(), "NO_FILES_UPLOADED");
        }
        List<String> allowedExtensions = Arrays.asList(FileAwsConstants.PNG, FileAwsConstants.JPEG, FileAwsConstants.JPG);
        List<String> fileUrls = new ArrayList<>();
        try {
            for (MultipartFile multipartFiles : multipartFile) {
                var bytes = multipartFiles.getBytes();
                InputStream inputStream = new ByteArrayInputStream(bytes);
                var fileExtension = FilenameUtils.getExtension(Objects.requireNonNull(multipartFiles.getOriginalFilename()));
                if (!allowedExtensions.contains(fileExtension))
                    throw new BadRequestAlertException("file invalid", HotelsEntity.class.toString(), "FILE_INVALID");
                String contentType = FileAwsConstants.CONTENT_TYPE_JPEG;
                if (fileExtension.equals(FileAwsConstants.PNG))
                    contentType = FileAwsConstants.CONTENT_TYPE_PNG;
                String filename = FileAwsConstants.FILE_HOTEL + System.currentTimeMillis() + "_image." + fileExtension;
                var linkImage = amazonS3StorageHandler.storeFilePublic(inputStream, filename, contentType);
                fileUrls.add(linkImage);
            }
        } catch (Exception e) {
            throw new BadRequestAlertException("No files uploaded", HotelsEntity.class.toString(), "NO_FILES_UPLOADED");
        }


        hotelsEntity.setId(custormCommonRepository.getSeqHotelId());
        long addressId = custormCommonRepository.insertAddress(request.getAddress1(), request.getAddress2(),
                request.getWardId());
        hotelsEntity.setAddressId(addressId);

        hotelRepository.insertHotel(hotelsEntity);
        custormCommonRepository.getSeqHotelImage();

        hotelRepository.insertHotelImage(hotelsEntity.getId(), fileUrls);

        hotelRepository.insertHotelService(hotelsEntity.getId(), request.getListHotelService());

        hotelRepository.updateEmployeeOfHotel(request.getBusinessOwner(), hotelsEntity.getId());

        return response;
    }

    public HotelsEntity parseRequestToHotelEntity(CreateHotelRequest request) {
        HotelsEntity hotelsEntity = new HotelsEntity();
        hotelsEntity.setName(request.getName());
        hotelsEntity.setStar(new BigDecimal(request.getStar()));
        hotelsEntity.setDescription(request.getDescription());
        hotelsEntity.setNote(request.getNote());
        hotelsEntity.setTaxCode(request.getTaxCode());
        hotelsEntity.setHotelPhoneNumber(request.getPhoneNumber());
        return hotelsEntity;
    }

    public ResponseListWithMetaData<SearchHotelResponse> searchHotel(SearchHotelRequest searchHotelRequest, CustomUserDetails principal) throws Exception {
        if (Objects.isNull(searchHotelRequest)) {
            searchHotelRequest = new SearchHotelRequest();
        }

        ResponseListWithMetaData<SearchHotelResponse> responseListData = new ResponseListWithMetaData<>();

        Integer totalRecords = hotelRepository.getTotalHotelOutput(searchHotelRequest, principal.getId());

        BaseSearchPagingDTO pagingDTO = searchHotelRequest.getSearchPaging();
        PageMetaDTO meta = commonService.settingPageMetaInfo(searchHotelRequest.getSearchPaging(),
                StringUtils.isEmpty(pagingDTO.getSortBy()) ? HEADER_SORT : Collections.singletonList(pagingDTO.getSortBy()),
                DEFAULT_SORT, totalRecords);

        List<SearchHotelResponse> listHotels = hotelRepository.searchHotel(searchHotelRequest, principal.getId());

        responseListData.setSuccessResponse(meta, listHotels);
        return responseListData;
    }

    public ResponseData<GetHotelResponseVerTwo> getHotelById(Long id) throws Exception {
        ResponseData<GetHotelResponseVerTwo> response = new ResponseData<>();
        GetHotelResponse data = hotelRepository.getHotelById(id);
        var ward = addressService.getWardById(data.getWardId());
        var district = addressService.getDistrictById(data.getDistrictId());
        var province = addressService.getProvinceById(data.getProvinceId());
        GetHotelResponseVerTwo responseVerTwo = new GetHotelResponseVerTwo();
        responseVerTwo.setBoId(data.getBoId());
        responseVerTwo.setId(data.getId());
        responseVerTwo.setName(data.getName());
        responseVerTwo.setStar(data.getStar());
        responseVerTwo.setLattitude(data.getLattitude());
        responseVerTwo.setLongtitude(data.getLongtitude());
        responseVerTwo.setDescription(data.getDescription());
        responseVerTwo.setNote(data.getNote());
        responseVerTwo.setTaxCode(data.getTaxCode());
        responseVerTwo.setStatus(data.getStatus());
        responseVerTwo.setAddress1(data.getAddress1());
        responseVerTwo.setAddress2(data.getAddress2());
        responseVerTwo.setWardName(ward.getData().getName());
        responseVerTwo.setDistrictName(district.getData().getName());
        responseVerTwo.setProvinceName(province.getData().getName());
        responseVerTwo.setWardId(data.getWardId());
        responseVerTwo.setDistrictId(data.getDistrictId());
        responseVerTwo.setProvinceId(data.getProvinceId());
        responseVerTwo.setListServiceId(hotelRepository.getServiceByHotelId(id));
        responseVerTwo.setListServiceName(data.getListHotelService());
        responseVerTwo.setBoName(data.getBoName());
        responseVerTwo.setPhoneNumber(data.getPhoneNumber());
        // Fetch the image URLs from the database
        List<String> roomImages = hotelRepository.getHotelsImage(data.getId());
        responseVerTwo.setListImages(roomImages);
        response.setData(responseVerTwo);
        return response;
    }

    public ResultResponse updateStatus(UpdateStatusRequest request) throws SQLException {
        ResultResponse response = new ResultResponse();
        GetHotelResponse hotelExist = hotelRepository.getHotelById(request.getId());
        if (hotelExist == null) {
            response.setMessage("Hotel not exist");
        } else {
            hotelRepository.updateStatus(request.getId(), request.getStatus());
            response.setMessage("Update status of marketing successfully");
        }
        return response;
    }

    public SearchHotelGuestReponse filterHotel(HotelFilterRequest filterParams) throws Exception {

        SearchHotelGuestReponse reponse = new SearchHotelGuestReponse();
        reponse.setRequest(filterParams);
        String searchParamProvince = null;
        String searchParamDistrict = null;

        if(StringUtils.isNotEmpty(filterParams.getSearch())){
            searchParamProvince = hotelRepository.getProvinceNameSuggest(filterParams.getSearch());
            searchParamDistrict = hotelRepository.getDistrictNameSuggest(filterParams.getSearch());
        }
        if (StringUtils.isEmpty(searchParamProvince) && StringUtils.isEmpty(searchParamDistrict)) {
            throw new BadRequestAlertException("No data", ENTITY_NAME, "NOT_FOUND");
        }
        List<GetHotelResponseVerThree> listHotels = hotelRepository.filterHotel(filterParams, searchParamProvince,searchParamDistrict);

        if (filterParams.getDateFrom() == null &&
                filterParams.getDateTo() == null &&
                filterParams.getHotelStar() == null &&
                filterParams.getRangePrice() == null &&
                filterParams.getNumberRoom() == null &&
                (filterParams.getSearch() == null || filterParams.getSearch().isEmpty()) &&
                filterParams.getNumberPeople() == null &&
                filterParams.getServiceList().getIncludesBreakfast() == null
        ) {
            reponse.setListHotel(listHotels);
            return reponse;
        }

        listHotels = listHotels.stream()
                .filter(hotel -> {
                    boolean meetsConditions = true;
                    if(filterParams.getServiceList() != null) {
                        if (filterParams.getServiceList().getIncludesBreakfast() != null) {
                            meetsConditions &= hotel.isIncludesBreakfast() == filterParams.getServiceList().getIncludesBreakfast();
                        }
                    }
                    if (filterParams.getHotelStar() != null) {
                        meetsConditions &= hotel.getStar() != null && hotel.getStar().equals(filterParams.getHotelStar());
                    }
                    // Add more conditions as needed
                    return meetsConditions;
                })
                .collect(Collectors.toList());

        if (listHotels.isEmpty()) {
            throw new BadRequestAlertException("No data", ENTITY_NAME, "NOT_FOUND");
        }
        reponse.setListHotel(listHotels);
        reponse.setRequest(filterParams);
        return reponse;
    }

    public List<String> getService(long id ){
       return hotelRepository.getAllServiceByHotelId(id);
    }

    public ResponseListData<SuggestHotelResponse> searchSuggestHotels(String province) throws Exception {
        ResponseListData<SuggestHotelResponse> responseListData = new ResponseListData();
        String provinceSearch = hotelRepository.getProvinceNameSuggest(province);
        responseListData.setListData(hotelRepository.searchHotelsSuggest(provinceSearch));
        return responseListData;
    }

    public ResponseListData<SuggestLocationResponse> searchSuggestLocation() throws Exception {
        ResponseListData<SuggestLocationResponse> responseListData = new ResponseListData();
        responseListData.setListData(hotelRepository.searchLocationSuggest());
        return responseListData;
    }

    public ResultResponse updateHotel(UpdateHotelRequest request, Object image1, Object image2, Object image3) throws Exception {
        ResultResponse response = new ResultResponse();

        boolean isTaxCodeExist  = hotelRepository.checkTaxCodeUpdate(request.getTaxCode(), request.getId());
        if(isTaxCodeExist) {
            throw new BadRequestAlertException("Tax code exist", HotelsEntity.class.toString(), "TAX_CODE_EXISTED");
        }
        boolean isPhoneExist = hotelRepository.checkPhoneNumberUpdate(request.getPhoneNumber(), request.getId());
        if(isPhoneExist) {
            throw new BadRequestAlertException("Phone number exist", HotelsEntity.class.toString(), "PHONE_NUMBER_EXIST");
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

        List<String> listImageOldDelete = hotelRepository.getListImageOldDelete(request.getId(), imageNoChange);

        hotelRepository.deleteListImage(request.getId(), imageNoChange);

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
            String filename = FileAwsConstants.FILE_HOTEL + System.currentTimeMillis() + "_image." + fileExtension;
            var linkImage = amazonS3StorageHandler.storeFilePublic(inputStream, filename, contentType);
            fileUrls.add(linkImage);
        }
        hotelRepository.insertHotelImage(request.getId(), fileUrls);


        hotelRepository.updateHotel(request);

        custormCommonRepository.updateAddress(request.getAddress1(), request.getAddress2(), request.getWardId(), request.getId());

        hotelRepository.deleteHotelService(request.getId());

        hotelRepository.insertHotelService(request.getId(), request.getListHotelService());

        GetHotelResponse hotel = hotelRepository.getHotelById(request.getId());

        if (!Objects.equals(hotel.getBoId(), request.getBusinessOwner())) {
            custormCommonRepository.updateHotelIdNull(hotel.getBoId());
            hotelRepository.updateEmployeeOfHotel(request.getBusinessOwner(), hotel.getId());
            custormCommonRepository.updateManager(hotel.getBoId(),request.getBusinessOwner());
        }

        return response;
    }
}
