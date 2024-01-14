package com.bootravel.service;

import com.bootravel.common.constant.FileAwsConstants;
import com.bootravel.common.constant.MasterDataConstants;
import com.bootravel.common.constant.MessageConstants;
import com.bootravel.common.dto.BaseSearchPagingDTO;
import com.bootravel.common.dto.PageMetaDTO;
import com.bootravel.common.security.jwt.dto.CustomUserDetails;
import com.bootravel.entity.PromotionsEntity;
import com.bootravel.entity.UsersEntity;
import com.bootravel.exception.BadRequestAlertException;
import com.bootravel.payload.requests.ChangePasswordRequest;
import com.bootravel.payload.requests.SearchUserRequest;
import com.bootravel.payload.requests.UpdateProfileRequest;
import com.bootravel.payload.responses.GetUserByIdResponse;
import com.bootravel.payload.responses.data.ResponseData;
import com.bootravel.payload.responses.data.ResponseListWithMetaData;
import com.bootravel.payload.responses.data.ResultResponse;
import com.bootravel.repository.CustomCommonRepository;
import com.bootravel.repository.UserRepository;
import com.bootravel.service.common.AmazonS3StorageHandler;
import com.bootravel.service.common.CommonService;
import com.bootravel.utils.I18n;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import net.bytebuddy.utility.RandomString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@Transactional
public class RegisteredUserService {
    private final UserRepository userRepository;
    private CustomCommonRepository custormCommonRepository;
    private CommonService commonService;
    private static final List<String> HEADER_SORT = Arrays.asList("userName");
    private static final String DEFAULT_SORT = "id";
    private static final String ENTITY_NAME = "RegisteredUserService";
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${mail.username}")
    private String username;

    @Autowired
    private AmazonS3StorageHandler amazonS3StorageHandler;

    public RegisteredUserService(UserRepository userRepository, CustomCommonRepository custormCommonRepository, CommonService commonService, JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.custormCommonRepository = custormCommonRepository;
        this.commonService = commonService;
        this.mailSender = mailSender;
    }

    public ResponseListWithMetaData<UsersEntity> getRegisteredUsersPagedAndFiltered(SearchUserRequest searchUserRequest) throws Exception {
        if (Objects.isNull(searchUserRequest)) {
            searchUserRequest = new SearchUserRequest();
        }
        ResponseListWithMetaData<UsersEntity> responseListData = new ResponseListWithMetaData<>();
        Integer totalRecords = custormCommonRepository.getTotalUserOutput(searchUserRequest,
                MasterDataConstants.ROLE_REGISTERED_USER);
        BaseSearchPagingDTO pagingDTO = searchUserRequest.getSearchPaging();
        PageMetaDTO meta = commonService.settingPageMetaInfo(searchUserRequest.getSearchPaging(),
                StringUtils.isEmpty(pagingDTO.getSortBy()) ? HEADER_SORT : Collections.singletonList(pagingDTO.getSortBy()),
                DEFAULT_SORT, totalRecords);
        List<UsersEntity> listUsers = custormCommonRepository.searchUser(searchUserRequest,
                MasterDataConstants.ROLE_REGISTERED_USER);
        responseListData.setSuccessResponse(meta, listUsers);
        return responseListData;
    }

    public ResponseData<UsersEntity> getDetailRegisteredUser(Long userId) {
        UsersEntity data = userRepository.findUserByRoleIdAndId(userId);
        ResponseData<UsersEntity> responseData = new ResponseData<>();
        responseData.setData(data);
        return responseData;
    }

    public ResponseData<String> register(UsersEntity user, String siteURL) throws UnsupportedEncodingException, MessagingException {
        String users = userRepository.checkInfoAlreadyExist(user.getEmail(), user.getPhoneNumber(), user.getUsername());
        ResponseData<String> responseData = new ResponseData<>();
        if (StringUtils.isNotEmpty(users)) {
            responseData.setCode(500);
            responseData.setMessage("User name or email is already in use!");
            return responseData;
        }
        boolean checkEmail = commonService.isValidEmail(user.getEmail());

        if (!checkEmail) {
            responseData.setCode(500);
            responseData.setMessage("Email is invalid");
            return responseData;
        }
        user.setRoleId(7L);
        user.setStatus(true);
        userRepository.insertUser(user);
        sendVerificationEmail(user, siteURL);
        responseData.setCode(200);
        responseData.setMessage("Register successfully");
        return responseData;
    }

    private void sendVerificationEmail(UsersEntity user, String siteURL) throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String fromAddress = username;
        String senderName = "TravelBoo";
        String subject = "Welcome to TravelBoo";
        String content = "Dear [[username]],<br>"
                + "Thank you to registed into our system<br>"
                + "TravelBoo";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);
        content = content.replace("[[username]]", user.getUsername());
        helper.setText(content, true);

        mailSender.send(message);
    }

    public boolean verify(String verificationCode) {
        UsersEntity user = userRepository.findByVerificationCode(verificationCode);

        if (user == null || user.getStatus() == true) {
            return false;
        } else {
            userRepository.updateVerificationCode(user.getVerificationCode(), null, true);
            return true;
        }

    }

    public ResponseData<UsersEntity> getDetailRegisteredUsers(Long userId) {
        // Call the getUsersById method from another class
        UsersEntity userData = userRepository.getUsersById(userId);

        // Rest of your code
        ResponseData<UsersEntity> responseData = new ResponseData<>();
        responseData.setData(userData);
        return responseData;
    }

    public ResponseData<UsersEntity> getDetailRegisteredUserByName(String name) {
        // Call the findByUsername method from another class
        UsersEntity userData = userRepository.findByUsername(name);
        // Rest of your code
        ResponseData<UsersEntity> responseData = new ResponseData<>();
        if(userData == null) {
            responseData.setData(null);
            return responseData;
        }
        responseData.setData(userData);
        return responseData;
    }

    public ResponseData<UsersEntity> getUserByRoleId(Long id) {
        // Call the findByUsername method from another class
        List<UsersEntity> userData = userRepository.findUserByRoleId(id);
        // Rest of your code
        ResponseData<UsersEntity> responseData = new ResponseData<>();
        return responseData;
    }

    public String updateStatusUsers(long userId, boolean newStatus) throws SQLException {
        boolean isExistUser = custormCommonRepository.isExistUser(userId);
        if (!isExistUser) {
           throw new BadRequestAlertException("Invalid ID",ENTITY_NAME,"invalid");
        } else {
            userRepository.updateRegisteredUsersStatus(userId,newStatus);
            return "SUCCESS";
        }

    }

    public void forgotPassword(String email) throws MessagingException, UnsupportedEncodingException {
        boolean isExist = userRepository.checkExistUserByEmail(email);
        if(!isExist) {
            throw new BadRequestAlertException("Email not exist", null ,null);
        }
        UsersEntity usersEntity = new UsersEntity();
        usersEntity.setEmail(email);
        String password  = RandomString.make(6);
        usersEntity.setPassword(passwordEncoder.encode(password));
        userRepository.updatePassword(usersEntity);
        sendForgotPassword(usersEntity.getEmail(), password);
    }

    private void sendForgotPassword(String email, String password) throws MessagingException, UnsupportedEncodingException {
        String toAddress = email;
        String fromAddress = username;
        String senderName = "TravelBoo";
        String subject = "Forgot password";
        String content = "Your password is [[password]]";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);
        content = content.replace("[[password]]", password);
        helper.setText(content, true);

        mailSender.send(message);
    }

    public ResponseData<GetUserByIdResponse> getRegisteredUserById(Long id) throws SQLException {
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

    public ResultResponse changePassword(ChangePasswordRequest changePasswordRequest) throws Exception {
        ResultResponse response = new ResultResponse();

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String oldPassword = customUserDetails.getPassword();
        String newPassword = passwordEncoder.encode(changePasswordRequest.getNewPassword());

        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), oldPassword)) {
            response.setMessage(I18n.get(MessageConstants.OLD_PASSWORD_ERROR));
        } else if (passwordEncoder.matches(changePasswordRequest.getNewPassword(), oldPassword)) {
            response.setMessage(I18n.get(MessageConstants.PASSWORD_NOT_CHANGE));
        } else {
            userRepository.changePassword(customUserDetails.getId(), newPassword);
        }
        return response;
    }

    public ResultResponse updateProfile(UpdateProfileRequest request, MultipartFile multipartFile) throws Exception {
        String fileUrls;
        if (multipartFile == null || multipartFile.isEmpty()) {
            GetUserByIdResponse getUserByIdResponse = custormCommonRepository.getUserById(request.getId());
            fileUrls = getUserByIdResponse.getAvatar();
        } else {
            List<String> allowedExtensions = Arrays.asList(FileAwsConstants.PNG, FileAwsConstants.JPEG, FileAwsConstants.JPG);

            var bytes = multipartFile.getBytes();
            InputStream inputStream = new ByteArrayInputStream(bytes);
            String originalFilename = Objects.requireNonNull(multipartFile.getOriginalFilename());
            String fileExtension = org.springframework.util.StringUtils.getFilenameExtension(originalFilename);

            if (!org.springframework.util.StringUtils.hasText(fileExtension) || !allowedExtensions.contains(fileExtension.toLowerCase())) {
                throw new BadRequestAlertException("File invalid", PromotionsEntity.class.toString(), "FILE_INVALID");
            }

            String contentType = FileAwsConstants.CONTENT_TYPE_JPEG;
            if (fileExtension.equalsIgnoreCase(FileAwsConstants.PNG)) {
                contentType = FileAwsConstants.CONTENT_TYPE_PNG;
            }

            String filename = FileAwsConstants.FILE_USER + System.currentTimeMillis() + "_image." + fileExtension;
            String linkImage = amazonS3StorageHandler.storeFilePublic(inputStream, filename, contentType);
            fileUrls = linkImage;
        }


        long addressId = custormCommonRepository.insertAddress(request.getAddress1(), request.getAddress2(),
                request.getWardId());
        UsersEntity usersEntity = new UsersEntity();
        usersEntity.setId(request.getId());
        usersEntity.setBirthDate(request.getDob());
        usersEntity.setFirstName(request.getFirstName());
        usersEntity.setLastName(request.getLastName());
        usersEntity.setPhoneNumber(request.getPhoneNumber());
        usersEntity.setGender(request.getGender());
        usersEntity.setAvatar(fileUrls);
        usersEntity.setAddressId(addressId);

        userRepository.updateProfile(usersEntity);

        return new ResultResponse();
    }

}
