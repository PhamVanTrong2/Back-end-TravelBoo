package com.bootravel.common.security.jwt.service;

import com.bootravel.common.constant.RoleConstants;
import com.bootravel.common.security.jwt.config.JwtUtil;
import com.bootravel.common.security.jwt.dto.CustomUserDetails;
import com.bootravel.common.security.jwt.entity.RefreshToken;
import com.bootravel.common.security.jwt.request.JwtRequest;
import com.bootravel.common.security.jwt.response.JwtResponse;
import com.bootravel.payload.responses.constant.ResponseType;
import com.bootravel.payload.responses.data.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


import java.util.Collections;

@Service
@Slf4j
public class JwtAuthenticationService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RefreshTokenService refreshTokenService;

    public ResponseData<JwtResponse> getToken(JwtRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassWord()));
        } catch (BadCredentialsException e) {
            log.info("Login error by: user name: " + request.getUserName() + " password: " + request.getPassWord());
            ResponseData<JwtResponse> responseData = new ResponseData<>();
            responseData.setType(ResponseType.ERROR);
            responseData.setMessage("Cannot find user login!");
            return responseData;
        }

        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return toDomain(token, refreshToken, user);
    }

    private ResponseData<JwtResponse> toDomain(String token, RefreshToken refreshToken, CustomUserDetails user) {
        JwtResponse response = new JwtResponse();
        String roleName = null;
        Long roleId = user.getRoleId();
        if( roleId == 1 ){
            roleName = RoleConstants.ADMIN;
        }
        if( roleId == 2 ){
            roleName = RoleConstants.MARKETING;
        }
        if( roleId == 3 ){
            roleName = RoleConstants.BUSINESS_ADMIN;
        }
        if( roleId == 4 ){
            roleName = RoleConstants.BUSINESS_OWNER;
        }
        if( roleId == 5 ){
            roleName = RoleConstants.BOOKING_STAFF;
        }
        if( roleId == 6 ){
            roleName = RoleConstants.TRANSACTION_STAFF;
        }
        if( roleId == 7 ){
            roleName = RoleConstants.USER;
        }
        response.setRoles(Collections.singletonList(roleName));
        response.setToken(token);
        response.setRefreshToken(refreshToken.getToken());
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        return new ResponseData<>(response);
    }

}
