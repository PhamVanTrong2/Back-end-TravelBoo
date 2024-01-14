package com.bootravel.service;

import com.bootravel.common.constant.MasterDataConstants;
import com.bootravel.common.constant.MessageConstants;
import com.bootravel.common.dto.BaseSearchPagingDTO;
import com.bootravel.common.dto.PageMetaDTO;
import com.bootravel.common.security.jwt.dto.CustomUserDetails;
import com.bootravel.entity.UsersEntity;
import com.bootravel.payload.requests.CreateStaffRequest;
import com.bootravel.payload.requests.SearchUserRequest;
import com.bootravel.payload.requests.UpdateStatusRequest;
import com.bootravel.payload.responses.GetUserByIdResponse;
import com.bootravel.payload.responses.RoomResponse;
import com.bootravel.payload.responses.SeachBoByManagerResponse;
import com.bootravel.payload.responses.data.ResponseData;
import com.bootravel.payload.responses.data.ResponseListData;
import com.bootravel.payload.responses.data.ResponseListWithMetaData;
import com.bootravel.payload.responses.data.ResultResponse;
import com.bootravel.repository.BusinessAdminRepository;
import com.bootravel.repository.CustomCommonRepository;
import com.bootravel.repository.UserRepository;
import com.bootravel.service.common.CommonService;
import com.bootravel.service.common.EmailService;

import com.bootravel.utils.I18n;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.sql.SQLException;
import java.util.*;

@Service
@Transactional
public class BusinessOwnerService {

    private static final String DEFAULT_SORT = "id";

    private static final List<String> HEADER_SORT = Arrays.asList("userName");

    private static final String USER_NAME = "#USERNAME";

    private static final String PASSWORD = "#PASSWORD";

    public static final String FULL_NAME = "#FULLNAME";

    private static final String TEMPLATE_MAIL_CREATE = "TEMPLATE_MAIL_CREATE_BO.html";

    private static final String SUBJECT = "YOUR ACCOUNT";


    @Autowired
    private BusinessAdminRepository businessAdminRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomCommonRepository custormCommonRepository;

    @Autowired
    private CommonService commonService;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private EmailService emailService;

    public ResultResponse createBo(CreateStaffRequest request) throws Exception {
        ResultResponse response = new ResultResponse();
        String fieldAlreadyExist = userRepository
                .checkInfoAlreadyExist(request.getEmail(), request.getPhoneNumber(), request.getUserName());
        if (StringUtils.isNotEmpty(fieldAlreadyExist)) {
            response.setMessage(I18n.get(MessageConstants.INFO_ALREADY_EXISTS, fieldAlreadyExist));
            response.setCode(409);
        } else {
            CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            long addressId = custormCommonRepository.insertAddress(request.getAddress1(), request.getAddress2(),
                    request.getWardId());
            long userId = custormCommonRepository.getSeqUserId();
            UsersEntity usersEntity = parseRequestToUsersEntity(request);
            usersEntity.setAddressId(addressId);
            usersEntity.setId(userId);
            String password = commonService.generateRandomPassword();
            usersEntity.setPassword(encoder.encode(password));


            Map<String, String> replacements = new HashMap<>();
            replacements.put(FULL_NAME, usersEntity.getFirstName() + usersEntity.getLastName());
            replacements.put(USER_NAME, usersEntity.getUsername());
            replacements.put(PASSWORD, password);

            emailService.sendEmail(TEMPLATE_MAIL_CREATE,
                    usersEntity.getEmail(),
                    SUBJECT,
                    null,
                    replacements
            );

            custormCommonRepository.insertUser(usersEntity);
            custormCommonRepository.insertEmployeeOf(userId, principal.getId());
        }


        return response;
    }


    public UsersEntity parseRequestToUsersEntity(CreateStaffRequest createBaRequest) {
        UsersEntity usersEntity = new UsersEntity();
        usersEntity.setUsername(createBaRequest.getUserName());
        usersEntity.setEmail(createBaRequest.getEmail());
        usersEntity.setBirthDate(createBaRequest.getDateOfBirth());
        usersEntity.setFirstName(createBaRequest.getFirstName());
        usersEntity.setLastName(createBaRequest.getLastName());
        usersEntity.setPhoneNumber(createBaRequest.getPhoneNumber());
        usersEntity.setGender(createBaRequest.getGender());
        usersEntity.setRoleId(MasterDataConstants.ROLE_BUSINESS_OWNER);
        usersEntity.setIdentification(createBaRequest.getIdentification());
        return usersEntity;
    }

    public ResultResponse updateStatusBo(UpdateStatusRequest request) throws Exception {
        ResultResponse response = new ResultResponse();
        boolean isExistUser = custormCommonRepository.isExistUser(request.getId());
        if (!isExistUser) {
            response.setMessage(I18n.get(MessageConstants.USER_NOT_EXIST));
        } else {
            UsersEntity usersEntity = new UsersEntity();
            usersEntity.setStatus(request.getStatus());
            custormCommonRepository.updateStatus(request.getId(), usersEntity);
            if(request.getStatus() == false){
                custormCommonRepository.updateHotelIdNull(request.getId());
            }
            response.setMessage("Update status of marketing successfully");
        }
        return response;
    }

    public ResponseData<GetUserByIdResponse> getBoById(Long id) throws SQLException {
        CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        long managerId = principal.getId();

        ResponseData<GetUserByIdResponse> response = new ResponseData<>();
        boolean isExistUser = custormCommonRepository.isExistUser(id);
        if (!isExistUser) {
            response.setMessage(I18n.get(MessageConstants.USER_NOT_EXIST));
        } else {
            GetUserByIdResponse data = custormCommonRepository.getEmployeeById(id, managerId);
            response.setData(data);
            return response;
        }
        return response;
    }

    public ResponseListWithMetaData<UsersEntity> searchListBo(SearchUserRequest searchUserRequest) throws Exception {
        CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        long managerId = principal.getId();

        if (Objects.isNull(searchUserRequest)) {
            searchUserRequest = new SearchUserRequest();
        }
        ResponseListWithMetaData<UsersEntity> responseListData = new ResponseListWithMetaData<>();
        Integer totalRecords = custormCommonRepository.getTotalEmployeeOutput(searchUserRequest,
                MasterDataConstants.ROLE_BUSINESS_OWNER, managerId);
        BaseSearchPagingDTO pagingDTO = searchUserRequest.getSearchPaging();
        PageMetaDTO meta = commonService.settingPageMetaInfo(searchUserRequest.getSearchPaging(),
                StringUtils.isEmpty(pagingDTO.getSortBy()) ? HEADER_SORT : Collections.singletonList(pagingDTO.getSortBy()),
                DEFAULT_SORT, totalRecords);
        List<UsersEntity> listUsers = custormCommonRepository.searchEmployee(searchUserRequest, MasterDataConstants.ROLE_BUSINESS_OWNER, managerId);
        responseListData.setSuccessResponse(meta, listUsers);
        return responseListData;
    }

    public ResponseListData<SeachBoByManagerResponse> seachBoByManager() throws Exception{
        ResponseListData<SeachBoByManagerResponse> responseListData = new ResponseListData<>();

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        responseListData.setListData(userRepository.searchBoByManager(userDetails.getId()));
        return  responseListData;
    }
}
