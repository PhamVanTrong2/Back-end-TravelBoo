package com.bootravel.service;

import com.bootravel.common.constant.MasterDataConstants;
import com.bootravel.common.constant.MessageConstants;
import com.bootravel.common.dto.BaseSearchPagingDTO;
import com.bootravel.common.dto.PageMetaDTO;
import com.bootravel.entity.UsersEntity;

import com.bootravel.payload.requests.CreateBaRequest;
import com.bootravel.payload.requests.SearchUserRequest;

import com.bootravel.payload.requests.UpdateStatusRequest;
import com.bootravel.payload.responses.GetUserByIdResponse;
import com.bootravel.payload.responses.data.ResponseData;
import com.bootravel.payload.responses.data.ResponseListWithMetaData;
import com.bootravel.payload.responses.data.ResultResponse;
import com.bootravel.repository.BusinessAdminRepository;
import com.bootravel.repository.CustomCommonRepository;
import com.bootravel.repository.UserRepository;
import com.bootravel.service.common.CommonService;
import com.bootravel.service.common.EmailService;
import com.bootravel.utils.FilesUtils;
import com.bootravel.utils.I18n;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.io.*;
import java.sql.SQLException;
import java.util.*;

@Service
@Slf4j
@Transactional
public class BusinessAdminService {


    private static final String DEFAULT_SORT = "id";

    private static final List<String> HEADER_SORT = Arrays.asList("userName");

    private static final String DOCX_TEMPLATE_CONTRACT = "TEMPLATE_CONTRACT.docx";

    private static final String COMPANY_NAME = "#COMPANYNAME";

    private static final String USER_NAME = "#USERNAME";

    private static final String PHONE_NUMBER = "#PHONENUMBER";

    private static final String PASSWORD = "#PASSWORD";

    public static final String FULL_NAME = "#FULLNAME";

    private static final String TEMPLATE_MAIL_CREATE = "TEMPLATE_MAIL_CREATE_BA.html";

    private static final String SUBJECT = "WELCOME TO US";

    private static final String FILE_NAME = "CONTRACT";


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

    public ResultResponse createBa(CreateBaRequest request) throws Exception {
        ResultResponse response = new ResultResponse();
        String fieldAlreadyExist = userRepository
                .checkInfoAlreadyExist(request.getEmail(), request.getPhoneNumber(), request.getUserName());
        if (StringUtils.isNotEmpty(fieldAlreadyExist)) {
            response.setMessage(I18n.get(MessageConstants.INFO_ALREADY_EXISTS, fieldAlreadyExist));
            response.setCode(409);
        } else {
            long addressId = custormCommonRepository.insertAddress(request.getAddress1(), request.getAddress2(),
                    request.getWardId());
            UsersEntity usersEntity = parseRequestToUsersEntity(request);
            usersEntity.setAddressId(addressId);

            String password = commonService.generateRandomPassword();
            usersEntity.setPassword(encoder.encode(password));

            long userId = custormCommonRepository.getSeqUserId();
            usersEntity.setId(userId);

            byte[] contractContent = getContentContract(usersEntity.getUsername(), usersEntity.getPhoneNumber(),
                    "", "");
            File fileAttach = new FilesUtils().createDocxFileFromBytes(contractContent, FILE_NAME);

            Map<String, String> replacements = new HashMap<>();
            replacements.put(FULL_NAME, usersEntity.getFirstName() + usersEntity.getLastName());
            replacements.put(USER_NAME, usersEntity.getUsername());
            replacements.put(PASSWORD, password);

            emailService.sendEmail(TEMPLATE_MAIL_CREATE,
                    usersEntity.getEmail(),
                    SUBJECT,
                    fileAttach,
                    replacements
            );

            custormCommonRepository.insertUser(usersEntity);

        }


        return response;
    }

    public UsersEntity parseRequestToUsersEntity(CreateBaRequest createBaRequest) {
        UsersEntity usersEntity = new UsersEntity();
        usersEntity.setUsername(createBaRequest.getUserName());
        usersEntity.setEmail(createBaRequest.getEmail());
        usersEntity.setBirthDate(createBaRequest.getDateOfBirth());
        usersEntity.setFirstName(createBaRequest.getFirstName());
        usersEntity.setLastName(createBaRequest.getLastName());
        usersEntity.setPhoneNumber(createBaRequest.getPhoneNumber());
        usersEntity.setGender(createBaRequest.getGender());
        usersEntity.setRoleId(MasterDataConstants.ROLE_BUSINESS_ADMIN);
        usersEntity.setIdentification(createBaRequest.getIdentification());
        return usersEntity;
    }

    public ResponseListWithMetaData<UsersEntity> searchListBa(SearchUserRequest searchUserRequest) throws Exception {
        if (Objects.isNull(searchUserRequest)) {
            searchUserRequest = new SearchUserRequest();
        }
        ResponseListWithMetaData<UsersEntity> responseListData = new ResponseListWithMetaData<>();
        Integer totalRecords = custormCommonRepository.getTotalUserOutput(searchUserRequest,
                MasterDataConstants.ROLE_BUSINESS_ADMIN);
        BaseSearchPagingDTO pagingDTO = searchUserRequest.getSearchPaging();
        PageMetaDTO meta = commonService.settingPageMetaInfo(searchUserRequest.getSearchPaging(),
                StringUtils.isEmpty(pagingDTO.getSortBy()) ? HEADER_SORT : Collections.singletonList(pagingDTO.getSortBy()),
                DEFAULT_SORT, totalRecords);
        List<UsersEntity> listUsers = custormCommonRepository.searchUser(searchUserRequest,
                MasterDataConstants.ROLE_BUSINESS_ADMIN);
        responseListData.setSuccessResponse(meta, listUsers);
        return responseListData;
    }

    public byte[] getContentContract(String userName, String phoneNumber, String businessName, String idCard) throws IOException {
        XWPFDocument docx = null;
        ByteArrayOutputStream bos = null;
        InputStream inputStream;
        try {
            inputStream = new ClassPathResource(MasterDataConstants.DOCX_TEMPLATE_PATH + DOCX_TEMPLATE_CONTRACT)
                    .getInputStream();
            docx = new XWPFDocument(OPCPackage.open(inputStream));
            for (XWPFParagraph paragraph : docx.getParagraphs()) {
                String text = paragraph.getText();
                if (text.contains(USER_NAME)) {
                    text = text.replaceAll(USER_NAME, userName);
                    paragraph.removeRun(0);
                    XWPFRun run = paragraph.createRun();
                    run.setText(text);
                    continue;
                }
                if (text.contains(COMPANY_NAME)) {
                    text = text.replaceAll(COMPANY_NAME, businessName);
                    paragraph.removeRun(0);
                    XWPFRun run = paragraph.createRun();
                    run.setText(text);
                    continue;
                }
                if (text.contains(PHONE_NUMBER)) {
                    text = text.replaceAll(PHONE_NUMBER, phoneNumber);
                    paragraph.removeRun(0);
                    XWPFRun run = paragraph.createRun();
                    run.setText(text);
                }


            }

            bos = new ByteArrayOutputStream();
            docx.write(bos);
            docx.close();
            bos.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (bos != null) {
                bos.close();
            }

            if (docx != null) {
                docx.close();
            }

        }
        return bos.toByteArray();
    }


    public ResultResponse updateStatusBa(UpdateStatusRequest request) throws SQLException {
        ResultResponse response = new ResultResponse();
        boolean isExistUser = custormCommonRepository.isExistUser(request.getId());
        if (!isExistUser) {
            response.setMessage(I18n.get(MessageConstants.USER_NOT_EXIST));
        } else {
            UsersEntity usersEntity = new UsersEntity();
            usersEntity.setStatus(request.getStatus());
            custormCommonRepository.updateStatus(request.getId(), usersEntity);
            response.setMessage("Update status of marketing successfully");
        }
        return response;
    }

    public ResponseData<GetUserByIdResponse> getBaById(Long id) throws SQLException {
        ResponseData<GetUserByIdResponse> response = new ResponseData<>();
        boolean isExistUser = custormCommonRepository.isExistUser(id);
        if (!isExistUser) {
            response.setMessage(I18n.get(MessageConstants.USER_NOT_EXIST));
        } else {
            GetUserByIdResponse data = custormCommonRepository.getUserById(id);
            response.setData(data);
            return response;
        }
        return response;
    }
}
