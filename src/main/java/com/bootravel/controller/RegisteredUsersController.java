package com.bootravel.controller;


import com.bootravel.common.security.jwt.config.JwtUtil;
import com.bootravel.common.security.jwt.dto.CustomUserDetails;
import com.bootravel.entity.UsersEntity;
import com.bootravel.exception.BadRequestAlertException;
import com.bootravel.payload.requests.*;
import com.bootravel.payload.requests.commonRequests.LoginRequest;
import com.bootravel.payload.requests.commonRequests.RegisterRequest;
import com.bootravel.payload.responses.GetUserByIdResponse;
import com.bootravel.payload.responses.data.ResponseData;
import com.bootravel.payload.responses.data.ResponseListWithMetaData;
import com.bootravel.payload.responses.data.ResultResponse;
import com.bootravel.repository.UserRepository;
import com.bootravel.service.RegisteredUserService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Null;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

@RestController
@Slf4j
@RequestMapping("/registered-users")
@Setter
public class RegisteredUsersController {
    private static final String ENTITY_NAME = "RegisteredController";
    private final UserRepository usersRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RegisteredUserService registeredUserService;

    public RegisteredUsersController(UserRepository usersRepository1, AuthenticationManager authenticationManager, JwtUtil jwtUtil, RegisteredUserService registeredUserService) {
        this.usersRepository = usersRepository1;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.registeredUserService = registeredUserService;
    }

    @PostMapping("/get-registered-user")
//    @PreAuthorize("hasAuthority('1')")
    public ResponseListWithMetaData<UsersEntity> getRegisteredUser(@RequestBody @Null SearchUserRequest searchUserRequest) throws Exception {
        return registeredUserService.getRegisteredUsersPagedAndFiltered(searchUserRequest);
    }

    @PostMapping("/update-user/{userId}")
    public ResponseEntity<UsersEntity> updateUser(@PathVariable Long userId, @RequestBody RegisterRequest updatedUser) {
        // Check if the user with userId exists
        UsersEntity existingUser = usersRepository.getUsersById(userId);

        if (existingUser != null) {
            // Update the user information
            UsersEntity updated = usersRepository.updateRegisteredUsers(updatedUser);

            if (updated != null) {
                return new ResponseEntity<>(updated, HttpStatus.OK); // Return 200 OK if the update is successful
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Return 400 Bad Request if the update fails
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Return 404 Not Found if the user is not found
        }
    }


    @PostMapping("/login")
    public ResponseData<String> login(@RequestBody LoginRequest loginRequest) {
        ResponseData<String> responseData = new ResponseData<>();

        if(loginRequest.getUserName() == null
                || loginRequest.getUserPassword() == null
        ) {
            responseData.setCode(401);
            responseData.setMessage("Username or password must not be null");
            return responseData;
        }
        // Retrieve user information from the database using the provided username
        ResponseData<UsersEntity> temp = registeredUserService.getDetailRegisteredUserByName(loginRequest.getUserName());
        if(temp == null) {
            responseData.setCode(401);
            responseData.setMessage("User not found");
            return responseData;
        }
        UsersEntity user = temp.getData();

        if (user != null && user.getStatus() == true) {
            // User with a status of 'true' found in the database
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getUserPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtil.generateToken((UserDetails) authentication.getPrincipal());
            responseData.setCode(200);
            responseData.setData(jwt);
            return responseData;
        } else if (user != null && !user.getStatus()) {
            responseData.setCode(401);
            responseData.setMessage("Account has been banned");
            return responseData;
        } else {
            responseData.setCode(401);
            responseData.setMessage("User not found");
            return responseData;
        }
    }

    @GetMapping("/verify")
    public String verifyUser(@Param("code") String code) {
        if (registeredUserService.verify(code)) {
            return "verify_success";
        } else {
            return "verify_fail";
        }
    }

    @PostMapping("/process-register")
    public ResponseData<String> processRegister(@RequestBody UsersEntity user, HttpServletRequest request)
            throws UnsupportedEncodingException, MessagingException {
        ResponseData<String> responseData = new ResponseData<>();

        if(user.getUsername() == null
            || user.getPassword() == null
            || user.getEmail() == null
            || user.getUsername().trim().isEmpty()
            || user.getPassword().trim().isEmpty()
            || user.getEmail().trim().isEmpty()
        ) {
            responseData.setCode(500);
            responseData.setMessage("User name or email, password is invalid!");
            return responseData;
        }

        responseData = registeredUserService.register(user, getSiteURL(request));
        return responseData;
    }

    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }

    @GetMapping("/get-registered-user-detail/{userId}")
    public ResponseEntity<UsersEntity> getDetailRegisteredUser(@PathVariable Long userId) {
        UsersEntity user = registeredUserService.getDetailRegisteredUser(userId).getData();

        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            throw new BadRequestAlertException("User not found with ID: " + userId, ENTITY_NAME, "usernotfound");
        }
    }

    @PostMapping("/update-status")
    public ResponseEntity<UsersEntity> updateRegisteredUsersStatus(@RequestBody UpdateStatusRequest request) throws SQLException {
        // Check if the user with userId exists
        UsersEntity existingUser = registeredUserService.getDetailRegisteredUsers(request.getId()).getData();
        if (existingUser != null) {
            String updatedUser = registeredUserService.updateStatusUsers(request.getId(), request.getStatus());

            if (updatedUser.equals("SUCCESS")) {
                return new ResponseEntity<>(HttpStatus.OK); // Return 200 OK if the update is successful
            } else {
                throw new BadRequestAlertException("Bad Request!", ENTITY_NAME, "badrequest");
            }
        } else {
            throw new BadRequestAlertException("Not Found!", ENTITY_NAME, "notfound");
        }
    }
    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestBody ForgotPasswordRequest request) throws MessagingException, UnsupportedEncodingException {
        registeredUserService.forgotPassword(request.getEmail());
        return "Success";
    }

    @GetMapping("/get-registered-user/{id}")
    public ResponseData<GetUserByIdResponse> getRegisteredUserById(@PathVariable("id") Long id) throws Exception {
        return registeredUserService.getRegisteredUserById(id);
    }

    @PostMapping("princial")
    public ResponseData<CustomUserDetails> principal() throws Exception {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ResponseData responseData = new ResponseData<>();
        responseData.setData(customUserDetails);
        return responseData;
    }

    @PostMapping("change-password")
    public ResultResponse changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) throws Exception {
        return registeredUserService.changePassword(changePasswordRequest);
    }

    @PostMapping("get-profile")
    public ResponseData<GetUserByIdResponse> getProfile() throws SQLException {
        CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = principal.getId();
        return registeredUserService.getRegisteredUserById(userId);
    }

    @PostMapping("update-profile")
    public ResultResponse updateProfile(
            String firstName,
            String lastName,
            String gender,
            String dob,
            String phoneNumber,
            Integer provinceId,
            Integer districtId,
            Integer wardId,
            String address1,
            String address2,
            @Null MultipartFile image
    ) throws Exception {
        CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setId(principal.getId());
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setGender(gender);
        request.setPhoneNumber(phoneNumber);
        request.setProvinceId(provinceId);
        request.setDistrictId(districtId);
        request.setWardId(wardId);
        request.setAddress1(address1);
        request.setAddress2(address2);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        request.setDob(formatter.parse(dob));
        return registeredUserService.updateProfile(request, image);
    }
}
