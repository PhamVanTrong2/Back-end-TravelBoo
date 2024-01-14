package com.bootravel.common.security.jwt.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class JwtRequest implements Serializable {

    private static final long serialVersionUID = 5926468583005150707L;
    @NotBlank
    private String userName;
    @NotBlank
    private String passWord;

}