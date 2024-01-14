package com.bootravel.common.security.jwt.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class JwtResponse implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;
    private String token;
    private String type = "Token";
    private String refreshToken;
    private Long id;
    private String username;
    private String email;
    private List<String> roles;

}