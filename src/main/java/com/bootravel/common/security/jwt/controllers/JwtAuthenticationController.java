package com.bootravel.common.security.jwt.controllers;


import com.bootravel.common.security.jwt.config.JwtUtil;
import com.bootravel.common.security.jwt.request.JwtRequest;
import com.bootravel.common.security.jwt.request.TokenRefreshRequest;
import com.bootravel.common.security.jwt.response.JwtResponse;
import com.bootravel.common.security.jwt.response.TokenRefreshResponse;
import com.bootravel.common.security.jwt.service.JwtAuthenticationService;
import com.bootravel.common.security.jwt.service.RefreshTokenService;
import com.bootravel.payload.responses.data.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.IOException;


@RestController
public class JwtAuthenticationController {


    @Autowired
    private JwtAuthenticationService service;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtUtil jwtUtil;


    @PostMapping("/login")
    public ResponseData<JwtResponse> createAuthenticationToken(@Valid @RequestBody JwtRequest request) {
        return service.getToken(request);
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<TokenRefreshResponse> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        return refreshTokenService.findByToken(request);
    }

//    @GetMapping("/check-token")
//    public ResponseData<JwtResponse> checkExpiredToken(@Valid @RequestBody String request) {
//        return jwtUtil.validateToken(request,);
//    }

}
