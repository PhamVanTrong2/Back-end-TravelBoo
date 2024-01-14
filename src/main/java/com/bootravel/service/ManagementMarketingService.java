package com.bootravel.service;

import com.bootravel.common.constant.MasterDataConstants;
import com.bootravel.common.constant.MessageConstants;
import com.bootravel.common.dto.BaseSearchPagingDTO;
import com.bootravel.common.dto.PageMetaDTO;
import com.bootravel.entity.UsersEntity;
import com.bootravel.payload.requests.CreateMarketingRequest;
import com.bootravel.payload.requests.SearchUserRequest;
import com.bootravel.payload.requests.UpdateStatusRequest;
import com.bootravel.payload.responses.GetUserByIdResponse;
import com.bootravel.payload.responses.data.ResponseData;
import com.bootravel.payload.responses.data.ResponseListWithMetaData;
import com.bootravel.payload.responses.data.ResultResponse;
import com.bootravel.repository.CustomCommonRepository;
import com.bootravel.repository.UserRepository;
import com.bootravel.service.common.CommonService;
import com.bootravel.service.common.EmailService;
import com.bootravel.utils.I18n;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.*;

@Slf4j
@Service
@Transactional
public class ManagementMarketingService {

    private static final String DEFAULT_SORT = "id";

    private static final List<String> HEADER_SORT = Arrays.asList("userName");

    private static final String COMPANY_NAME = "#COMPANYNAME";

    private static final String USER_NAME = "#USERNAME";

    private static final String PHONE_NUMBER = "#PHONENUMBER";

    private static final String PASSWORD = "#PASSWORD";

    public static final String FULL_NAME = "#FULLNAME";

    private static final String TEMPLATE_MAIL_CREATE = "TEMPLATE_MAIL_CREATE_MARKETING.html";

    private static final String SUBJECT = "WELCOME TO US";

    private static final String FILE_NAME = "CONTRACT";

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomCommonRepository customCommonRepository;

    @Autowired
    private CommonService commonService;

    @Autowired
    private EmailService emailService;

    public ResultResponse createMarketing(CreateMarketingRequest request) throws Exception {
        ResultResponse response = new ResultResponse();
        String fieldAlreadyExist = userRepository.checkInfoAlreadyExist(request.getEmail(), request.getPhoneNumber(), request.getUsername());
        if (StringUtils.isNotEmpty(fieldAlreadyExist)) {
            response.setMessage(I18n.get(MessageConstants.INFO_ALREADY_EXISTS, fieldAlreadyExist));
            response.setCode(409);
        } else {
            long addressId = customCommonRepository.insertAddress(request.getAddress1(), request.getAddress2(),
                    request.getWardId());
            long userId = customCommonRepository.getSeqUserId();

            UsersEntity usersEntity = parseRequestToUsersEntity(request);
            usersEntity.setAddressId(addressId);
            usersEntity.setId(userId);

            String password = commonService.generateRandomPassword();
            usersEntity.setPassword(passwordEncoder.encode(password));

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
            
            customCommonRepository.insertUser(usersEntity);
            response.setMessage("Create new marketing successfully");
        }
        return response;
    }

    public ResultResponse updateMarketing(UpdateStatusRequest request) throws SQLException {
        ResultResponse response = new ResultResponse();
        boolean isExistUser = customCommonRepository.isExistUser(request.getId());
        if (!isExistUser) {
            response.setMessage(I18n.get(MessageConstants.USER_NOT_EXIST));
        } else {
            UsersEntity usersEntity = new UsersEntity();
            usersEntity.setStatus(request.getStatus());
            customCommonRepository.updateStatus(request.getId(), usersEntity);
            response.setMessage("Update status of marketing successfully");
        }
        return response;
    }

    public UsersEntity parseRequestToUsersEntity(CreateMarketingRequest request) {
        UsersEntity usersEntity = new UsersEntity();
        usersEntity.setUsername(request.getUsername());
        usersEntity.setEmail(request.getEmail());
        usersEntity.setBirthDate(request.getBirthDate());
        usersEntity.setFirstName(request.getFirstName());
        usersEntity.setLastName(request.getLastName());
        usersEntity.setPhoneNumber(request.getPhoneNumber());
        usersEntity.setGender(request.getGender());
        usersEntity.setRoleId(MasterDataConstants.ROLE_MARKETING);
        return usersEntity;
    }

    public ResponseData<GetUserByIdResponse> getMarketingById(Long id) throws SQLException {
        ResponseData<GetUserByIdResponse> response = new ResponseData<>();
        boolean isExistUser = customCommonRepository.isExistUser(id);
        if (!isExistUser) {
            response.setMessage(I18n.get(MessageConstants.USER_NOT_EXIST));
        } else {
            GetUserByIdResponse data = customCommonRepository.getUserById(id);
            response.setData(data);
            return response;
        }
        return response;
    }

    public ResponseListWithMetaData<UsersEntity> searchListMarketing(SearchUserRequest searchUserRequest) throws Exception {
        if (Objects.isNull(searchUserRequest)) {
            searchUserRequest = new SearchUserRequest();
        }
        ResponseListWithMetaData<UsersEntity> responseListData = new ResponseListWithMetaData<>();
        Integer totalRecords = customCommonRepository.getTotalUserOutput(searchUserRequest,
                                                                            MasterDataConstants.ROLE_MARKETING);
        BaseSearchPagingDTO pagingDTO = searchUserRequest.getSearchPaging();
        PageMetaDTO meta = commonService.settingPageMetaInfo(searchUserRequest.getSearchPaging(),
                StringUtils.isEmpty(pagingDTO.getSortBy()) ? HEADER_SORT : Collections.singletonList(pagingDTO.getSortBy()),
                DEFAULT_SORT, totalRecords);
        List<UsersEntity> listUsers = customCommonRepository.searchUser(searchUserRequest, MasterDataConstants.ROLE_MARKETING);
        responseListData.setSuccessResponse(meta, listUsers);
        return responseListData;
    }
}
