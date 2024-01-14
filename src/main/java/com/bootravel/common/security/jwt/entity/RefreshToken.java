package com.bootravel.common.security.jwt.entity;

import com.bootravel.entity.UsersEntity;
import lombok.Data;

import java.time.Instant;

@Data
public class RefreshToken {

    private long id;

    private UsersEntity usersEntity;

    private String token;

    private Instant expiryDate;

}