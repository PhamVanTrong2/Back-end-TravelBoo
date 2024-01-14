package com.bootravel.payload.responses;

import com.bootravel.common.security.jwt.dto.CustomUserDetails;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;

@Data
@AllArgsConstructor
public class UserResponse {
    private String userName;
    private String password;
    private long roleId;

    public UserResponse(CustomUserDetails userDetails) {
        if (Objects.nonNull(userDetails)) {
            this.userName = userDetails.getUsername();
            this.password = userDetails.getPassword();
            this.roleId = userDetails.getRoleId();
        }
    }
}
